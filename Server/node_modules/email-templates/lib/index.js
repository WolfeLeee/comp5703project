"use strict";

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

function asyncGeneratorStep(gen, resolve, reject, _next, _throw, key, arg) { try { var info = gen[key](arg); var value = info.value; } catch (error) { reject(error); return; } if (info.done) { resolve(value); } else { Promise.resolve(value).then(_next, _throw); } }

function _asyncToGenerator(fn) { return function () { var self = this, args = arguments; return new Promise(function (resolve, reject) { var gen = fn.apply(self, args); function _next(value) { asyncGeneratorStep(gen, resolve, reject, _next, _throw, "next", value); } function _throw(err) { asyncGeneratorStep(gen, resolve, reject, _next, _throw, "throw", err); } _next(undefined); }); }; }

const fs = require('fs');

const path = require('path');

const juice = require('juice');

const debug = require('debug')('email-templates');

const htmlToText = require('html-to-text');

const I18N = require('@ladjs/i18n');

const autoBind = require('auto-bind');

const nodemailer = require('nodemailer');

const consolidate = require('consolidate');

const previewEmail = require('preview-email');

const _ = require('lodash');

const _Promise = require('bluebird');

const s = require('underscore.string');

const getPaths = require('get-paths'); // promise version of `juice.juiceResources`


const juiceResources = (html, options) => {
  return new _Promise((resolve, reject) => {
    juice.juiceResources(html, options, (err, html) => {
      if (err) return reject(err);
      resolve(html);
    });
  });
};

const env = (process.env.NODE_ENV || 'development').toLowerCase();

const stat = _Promise.promisify(fs.stat);

const readFile = _Promise.promisify(fs.readFile);

class Email {
  constructor(config = {}) {
    debug('config passed %O', config); // 2.x backwards compatible support

    if (config.juiceOptions) {
      config.juiceResources = config.juiceOptions;
      delete config.juiceOptions;
    }

    if (config.disableJuice) {
      config.juice = false;
      delete config.disableJuice;
    }

    if (config.render) {
      config.customRender = true;
    }

    this.config = _.merge({
      views: {
        // directory where email templates reside
        root: path.resolve('emails'),
        options: {
          // default file extension for template
          extension: 'pug',
          map: {
            hbs: 'handlebars',
            njk: 'nunjucks'
          },
          engineSource: consolidate
        },
        // locals to pass to templates for rendering
        locals: {
          // pretty is automatically set to `false` for subject/text
          pretty: true
        }
      },
      // <https://nodemailer.com/message/>
      message: {},
      send: !['development', 'test'].includes(env),
      preview: env === 'development',
      // <https://github.com/ladjs/i18n>
      // set to an object to configure and enable it
      i18n: false,
      // pass a custom render function if necessary
      render: this.render.bind(this),
      customRender: false,
      // force text-only rendering of template (disregards template folder)
      textOnly: false,
      // <https://github.com/werk85/node-html-to-text>
      htmlToText: {
        ignoreImage: true
      },
      subjectPrefix: false,
      // <https://github.com/Automattic/juice>
      juice: true,
      juiceResources: {
        preserveImportant: true,
        webResources: {
          relativeTo: path.resolve('build'),
          images: false
        }
      },
      // pass a transport configuration object or a transport instance
      // (e.g. an instance is created via `nodemailer.createTransport`)
      // <https://nodemailer.com/transports/>
      transport: {}
    }, config); // override existing method

    this.render = this.config.render;
    if (!_.isFunction(this.config.transport.sendMail)) this.config.transport = nodemailer.createTransport(this.config.transport);
    debug('transformed config %O', this.config);
    autoBind(this);
  } // shorthand use of `juiceResources` with the config
  // (mainly for custom renders like from a database)


  juiceResources(html) {
    return juiceResources(html, this.config.juiceResources);
  } // a simple helper function that gets the actual file path for the template


  getTemplatePath(template) {
    var _this = this;

    return _asyncToGenerator(function* () {
      const _ref = path.isAbsolute(template) ? [path.dirname(template), path.basename(template)] : [_this.config.views.root, template],
            _ref2 = _slicedToArray(_ref, 2),
            root = _ref2[0],
            view = _ref2[1];

      const paths = yield getPaths(root, view, _this.config.views.options.extension);
      const filePath = path.resolve(root, paths.rel);
      return {
        filePath,
        paths
      };
    })();
  } // returns true or false if a template exists
  // (uses same look-up approach as `render` function)


  templateExists(view) {
    var _this2 = this;

    return _asyncToGenerator(function* () {
      try {
        const _ref3 = yield _this2.getTemplatePath(view),
              filePath = _ref3.filePath;

        const stats = yield stat(filePath);
        if (!stats.isFile()) throw new Error(`${filePath} was not a file`);
        return true;
      } catch (err) {
        debug('templateExists', err);
        return false;
      }
    })();
  } // promise version of consolidate's render
  // inspired by koa-views and re-uses the same config
  // <https://github.com/queckezz/koa-views>


  render(view, locals = {}) {
    var _this3 = this;

    return _asyncToGenerator(function* () {
      const _this3$config$views$o = _this3.config.views.options,
            map = _this3$config$views$o.map,
            engineSource = _this3$config$views$o.engineSource;

      const _ref4 = yield _this3.getTemplatePath(view),
            filePath = _ref4.filePath,
            paths = _ref4.paths;

      if (paths.ext === 'html' && !map) {
        const res = yield readFile(filePath, 'utf8');
        return res;
      }

      const engineName = map && map[paths.ext] ? map[paths.ext] : paths.ext;
      const renderFn = engineSource[engineName];
      if (!engineName || !renderFn) throw new Error(`Engine not found for the ".${paths.ext}" file extension`);

      if (_.isObject(_this3.config.i18n)) {
        const i18n = new I18N(Object.assign({}, _this3.config.i18n, {
          register: locals
        })); // support `locals.user.last_locale`
        // (e.g. for <https://lad.js.org>)

        if (_.isObject(locals.user) && _.isString(locals.user.last_locale)) locals.locale = locals.user.last_locale;
        if (_.isString(locals.locale)) i18n.setLocale(locals.locale);
      }

      const res = yield _Promise.promisify(renderFn)(filePath, locals); // transform the html with juice using remote paths
      // google now supports media queries
      // https://developers.google.com/gmail/design/reference/supported_css

      if (!_this3.config.juice) return res;
      const html = yield _this3.juiceResources(res);
      return html;
    })();
  } // TODO: this needs refactored
  // so that we render templates asynchronously


  renderAll(template, locals = {}, message = {}) {
    var _this4 = this;

    return _asyncToGenerator(function* () {
      let subjectTemplateExists = _this4.config.customRender;
      let htmlTemplateExists = _this4.config.customRender;
      let textTemplateExists = _this4.config.customRender;

      if (template && !_this4.config.customRender) {
        var _ref5 = yield _Promise.all([_this4.templateExists(`${template}/subject`), _this4.templateExists(`${template}/html`), _this4.templateExists(`${template}/text`)]);

        var _ref6 = _slicedToArray(_ref5, 3);

        subjectTemplateExists = _ref6[0];
        htmlTemplateExists = _ref6[1];
        textTemplateExists = _ref6[2];
      }

      if (!message.subject && subjectTemplateExists) {
        message.subject = yield _this4.render(`${template}/subject`, Object.assign({}, locals, {
          pretty: false
        }));
        message.subject = message.subject.trim();
      }

      if (message.subject && _this4.config.subjectPrefix) message.subject = _this4.config.subjectPrefix + message.subject;
      if (!message.html && htmlTemplateExists) message.html = yield _this4.render(`${template}/html`, locals);
      if (!message.text && textTemplateExists) message.text = yield _this4.render(`${template}/text`, Object.assign({}, locals, {
        pretty: false
      }));
      if (_this4.config.htmlToText && message.html && !message.text) // we'd use nodemailer-html-to-text plugin
        // but we really don't need to support cid
        // <https://github.com/andris9/nodemailer-html-to-text>
        message.text = htmlToText.fromString(message.html, _this4.config.htmlToText); // if we only want a text-based version of the email

      if (_this4.config.textOnly) delete message.html; // if no subject, html, or text content exists then we should
      // throw an error that says at least one must be found
      // otherwise the email would be blank (defeats purpose of email-templates)

      if (s.isBlank(message.subject) && s.isBlank(message.text) && s.isBlank(message.html) && _.isArray(message.attachments) && _.isEmpty(message.attachments)) throw new Error(`No content was passed for subject, html, text, nor attachments message props. Check that the files for the template "${template}" exist.`);
      return message;
    })();
  }

  send(options = {}) {
    var _this5 = this;

    return _asyncToGenerator(function* () {
      options = Object.assign({
        template: '',
        message: {},
        locals: {}
      }, options);
      let _options = options,
          template = _options.template,
          message = _options.message,
          locals = _options.locals;
      const attachments = message.attachments || _this5.config.message.attachments || [];
      message = _.defaultsDeep({}, _.omit(message, 'attachments'), _.omit(_this5.config.message, 'attachments'));
      locals = _.defaultsDeep({}, _this5.config.views.locals, locals);
      if (attachments) message.attachments = attachments;
      debug('template %s', template);
      debug('message %O', message);
      debug('locals (keys only): %O', Object.keys(locals)); // get all available templates

      const obj = yield _this5.renderAll(template, locals, message); // assign the object variables over to the message

      Object.assign(message, obj);

      if (_this5.config.preview) {
        debug('using `preview-email` to preview email');
        if (_.isObject(_this5.config.preview)) yield previewEmail(message, null, true, _this5.config.preview);else yield previewEmail(message);
      }

      if (!_this5.config.send) {
        debug('send disabled so we are ensuring JSONTransport'); // <https://github.com/nodemailer/nodemailer/issues/798>
        // if (this.config.transport.name !== 'JSONTransport')

        _this5.config.transport = nodemailer.createTransport({
          jsonTransport: true
        });
      }

      const res = yield _this5.config.transport.sendMail(message);
      debug('message sent');
      res.originalMessage = message;
      return res;
    })();
  }

}

module.exports = Email;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi4uL3NyYy9pbmRleC5qcyJdLCJuYW1lcyI6WyJmcyIsInJlcXVpcmUiLCJwYXRoIiwianVpY2UiLCJkZWJ1ZyIsImh0bWxUb1RleHQiLCJJMThOIiwiYXV0b0JpbmQiLCJub2RlbWFpbGVyIiwiY29uc29saWRhdGUiLCJwcmV2aWV3RW1haWwiLCJfIiwiUHJvbWlzZSIsInMiLCJnZXRQYXRocyIsImp1aWNlUmVzb3VyY2VzIiwiaHRtbCIsIm9wdGlvbnMiLCJyZXNvbHZlIiwicmVqZWN0IiwiZXJyIiwiZW52IiwicHJvY2VzcyIsIk5PREVfRU5WIiwidG9Mb3dlckNhc2UiLCJzdGF0IiwicHJvbWlzaWZ5IiwicmVhZEZpbGUiLCJFbWFpbCIsImNvbnN0cnVjdG9yIiwiY29uZmlnIiwianVpY2VPcHRpb25zIiwiZGlzYWJsZUp1aWNlIiwicmVuZGVyIiwiY3VzdG9tUmVuZGVyIiwibWVyZ2UiLCJ2aWV3cyIsInJvb3QiLCJleHRlbnNpb24iLCJtYXAiLCJoYnMiLCJuamsiLCJlbmdpbmVTb3VyY2UiLCJsb2NhbHMiLCJwcmV0dHkiLCJtZXNzYWdlIiwic2VuZCIsImluY2x1ZGVzIiwicHJldmlldyIsImkxOG4iLCJiaW5kIiwidGV4dE9ubHkiLCJpZ25vcmVJbWFnZSIsInN1YmplY3RQcmVmaXgiLCJwcmVzZXJ2ZUltcG9ydGFudCIsIndlYlJlc291cmNlcyIsInJlbGF0aXZlVG8iLCJpbWFnZXMiLCJ0cmFuc3BvcnQiLCJpc0Z1bmN0aW9uIiwic2VuZE1haWwiLCJjcmVhdGVUcmFuc3BvcnQiLCJnZXRUZW1wbGF0ZVBhdGgiLCJ0ZW1wbGF0ZSIsImlzQWJzb2x1dGUiLCJkaXJuYW1lIiwiYmFzZW5hbWUiLCJ2aWV3IiwicGF0aHMiLCJmaWxlUGF0aCIsInJlbCIsInRlbXBsYXRlRXhpc3RzIiwic3RhdHMiLCJpc0ZpbGUiLCJFcnJvciIsImV4dCIsInJlcyIsImVuZ2luZU5hbWUiLCJyZW5kZXJGbiIsImlzT2JqZWN0IiwiT2JqZWN0IiwiYXNzaWduIiwicmVnaXN0ZXIiLCJ1c2VyIiwiaXNTdHJpbmciLCJsYXN0X2xvY2FsZSIsImxvY2FsZSIsInNldExvY2FsZSIsInJlbmRlckFsbCIsInN1YmplY3RUZW1wbGF0ZUV4aXN0cyIsImh0bWxUZW1wbGF0ZUV4aXN0cyIsInRleHRUZW1wbGF0ZUV4aXN0cyIsImFsbCIsInN1YmplY3QiLCJ0cmltIiwidGV4dCIsImZyb21TdHJpbmciLCJpc0JsYW5rIiwiaXNBcnJheSIsImF0dGFjaG1lbnRzIiwiaXNFbXB0eSIsImRlZmF1bHRzRGVlcCIsIm9taXQiLCJrZXlzIiwib2JqIiwianNvblRyYW5zcG9ydCIsIm9yaWdpbmFsTWVzc2FnZSIsIm1vZHVsZSIsImV4cG9ydHMiXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7O0FBQUEsTUFBTUEsRUFBRSxHQUFHQyxPQUFPLENBQUMsSUFBRCxDQUFsQjs7QUFDQSxNQUFNQyxJQUFJLEdBQUdELE9BQU8sQ0FBQyxNQUFELENBQXBCOztBQUNBLE1BQU1FLEtBQUssR0FBR0YsT0FBTyxDQUFDLE9BQUQsQ0FBckI7O0FBQ0EsTUFBTUcsS0FBSyxHQUFHSCxPQUFPLENBQUMsT0FBRCxDQUFQLENBQWlCLGlCQUFqQixDQUFkOztBQUNBLE1BQU1JLFVBQVUsR0FBR0osT0FBTyxDQUFDLGNBQUQsQ0FBMUI7O0FBQ0EsTUFBTUssSUFBSSxHQUFHTCxPQUFPLENBQUMsYUFBRCxDQUFwQjs7QUFDQSxNQUFNTSxRQUFRLEdBQUdOLE9BQU8sQ0FBQyxXQUFELENBQXhCOztBQUNBLE1BQU1PLFVBQVUsR0FBR1AsT0FBTyxDQUFDLFlBQUQsQ0FBMUI7O0FBQ0EsTUFBTVEsV0FBVyxHQUFHUixPQUFPLENBQUMsYUFBRCxDQUEzQjs7QUFDQSxNQUFNUyxZQUFZLEdBQUdULE9BQU8sQ0FBQyxlQUFELENBQTVCOztBQUNBLE1BQU1VLENBQUMsR0FBR1YsT0FBTyxDQUFDLFFBQUQsQ0FBakI7O0FBQ0EsTUFBTVcsUUFBTyxHQUFHWCxPQUFPLENBQUMsVUFBRCxDQUF2Qjs7QUFDQSxNQUFNWSxD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QixHQUFQO0FBckR1QjtBQXNEeEI7O0FBelJTOztBQTRSWitCLE1BQU0sQ0FBQ0MsT0FBUCxHQUFpQmhGLEtBQWpCIiwic291cmNlc0NvbnRlbnQiOlsiY29uc3QgZnMgPSByZXF1aXJlKCdmcycpO1xuY29uc3QgcGF0aCA9IHJlcXVpcmUoJ3BhdGgnKTtcbmNvbnN0IGp1aWNlID0gcmVxdWlyZSgnanVpY2UnKTtcbmNvbnN0IGRlYnVnID0gcmVxdWlyZSgnZGVidWcnKSgnZW1haWwtdGVtcGxhdGVzJyk7XG5jb25zdCBodG1sVG9UZXh0ID0gcmVxdWlyZSgnaHRtbC10by10ZXh0Jyk7XG5jb25zdCBJMThOID0gcmVxdWlyZSgnQGxhZGpzL2kxOG4nKTtcbmNvbnN0IGF1dG9CaW5kID0gcmVxdWlyZSgnYXV0by1iaW5kJyk7XG5jb25zdCBub2RlbWFpbGVyID0gcmVxdWlyZSgnbm9kZW1haWxlcicpO1xuY29uc3QgY29uc29saWRhdGUgPSByZXF1aXJlKCdjb25zb2xpZGF0ZScpO1xuY29uc3QgcHJldmlld0VtYWlsID0gcmVxdWlyZSgncHJldmlldy1lbWFpbCcpO1xuY29uc3QgXyA9IHJlcXVpcmUoJ2xvZGFzaCcpO1xuY29uc3QgUHJvbWlzZSA9IHJlcXVpcmUoJ2JsdWViaXJkJyk7XG5jb25zdCBzID0gcmVxdWlyZSgndW5kZXJzY29yZS5zdHJpbmcnKTtcblxuY29uc3QgZ2V0UGF0aHMgPSByZXF1aXJlKCdnZXQtcGF0aHMnKTtcblxuLy8gcHJvbWlzZSB2ZXJzaW9uIG9mIGBqdWljZS5qdWljZVJlc291cmNlc2BcbmNvbnN0IGp1aWNlUmVzb3VyY2VzID0gKGh0bWwsIG9wdGlvbnMpID0+IHtcbiAgcmV0dXJuIG5ldyBQcm9taXNlKChyZXNvbHZlLCByZWplY3QpID0+IHtcbiAgICBqdWljZS5qdWljZVJlc291cmNlcyhodG1sLCBvcHRpb25zLCAoZXJyLCBodG1sKSA9PiB7XG4gICAgICBpZiAoZXJyKSByZXR1cm4gcmVqZWN0KGVycik7XG4gICAgICByZXNvbHZlKGh0bWwpO1xuICAgIH0pO1xuICB9KTtcbn07XG5cbmNvbnN0IGVudiA9IChwcm9jZXNzLmVudi5OT0RFX0VOViB8fCAnZGV2ZWxvcG1lbnQnKS50b0xvd2VyQ2FzZSgpO1xuY29uc3Qgc3RhdCA9IFByb21pc2UucHJvbWlzaWZ5KGZzLnN0YXQpO1xuY29uc3QgcmVhZEZpbGUgPSBQcm9taXNlLnByb21pc2lmeShmcy5yZWFkRmlsZSk7XG5cbmNsYXNzIEVtYWlsIHtcbiAgY29uc3RydWN0b3IoY29uZmlnID0ge30pIHtcbiAgICBkZWJ1ZygnY29uZmlnIHBhc3NlZCAlTycsIGNvbmZpZyk7XG5cbiAgICAvLyAyLnggYmFja3dhcmRzIGNvbXBhdGlibGUgc3VwcG9ydFxuICAgIGlmIChjb25maWcuanVpY2VPcHRpb25zKSB7XG4gICAgICBjb25maWcuanVpY2VSZXNvdXJjZXMgPSBjb25maWcuanVpY2VPcHRpb25zO1xuICAgICAgZGVsZXRlIGNvbmZpZy5qdWljZU9wdGlvbnM7XG4gICAgfVxuXG4gICAgaWYgKGNvbmZpZy5kaXNhYmxlSnVpY2UpIHtcbiAgICAgIGNvbmZpZy5qdWljZSA9IGZhbHNlO1xuICAgICAgZGVsZXRlIGNvbmZpZy5kaXNhYmxlSnVpY2U7XG4gICAgfVxuXG4gICAgaWYgKGNvbmZpZy5yZW5kZXIpIHtcbiAgICAgIGNvbmZpZy5jdXN0b21SZW5kZXIgPSB0cnVlO1xuICAgIH1cblxuICAgIHRoaXMuY29uZmlnID0gXy5tZXJnZShcbiAgICAgIHtcbiAgICAgICAgdmlld3M6IHtcbiAgICAgICAgICAvLyBkaXJlY3Rvcnkgd2hlcmUgZW1haWwgdGVtcGxhdGVzIHJlc2lkZVxuICAgICAgICAgIHJvb3Q6IHBhdGgucmVzb2x2ZSgnZW1haWxzJyksXG4gICAgICAgICAgb3B0aW9uczoge1xuICAgICAgICAgICAgLy8gZGVmYXVsdCBmaWxlIGV4dGVuc2lvbiBmb3IgdGVtcGxhdGVcbiAgICAgICAgICAgIGV4dGVuc2lvbjogJ3B1ZycsXG4gICAgICAgICAgICBtYXA6IHtcbiAgICAgICAgICAgICAgaGJzOiAnaGFuZGxlYmFycycsXG4gICAgICAgICAgICAgIG5qazogJ251bmp1Y2tzJ1xuICAgICAgICAgICAgfSxcbiAgICAgICAgICAgIGVuZ2luZVNvdXJjZTogY29uc29saWRhdGVcbiAgICAgICAgICB9LFxuICAgICAgICAgIC8vIGxvY2FscyB0byBwYXNzIHRvIHRlbXBsYXRlcyBmb3IgcmVuZGVyaW5nXG4gICAgICAgICAgbG9jYWxzOiB7XG4gICAgICAgICAgICAvLyBwcmV0dHkgaXMgYXV0b21hdGljYWxseSBzZXQgdG8gYGZhbHNlYCBmb3Igc3ViamVjdC90ZXh0XG4gICAgICAgICAgICBwcmV0dHk6IHRydWVcbiAgICAgICAgICB9XG4gICAgICAgIH0sXG4gICAgICAgIC8vIDxodHRwczovL25vZGVtYWlsZXIuY29tL21lc3NhZ2UvPlxuICAgICAgICBtZXNzYWdlOiB7fSxcbiAgICAgICAgc2VuZDogIVsnZGV2ZWxvcG1lbnQnLCAndGVzdCddLmluY2x1ZGVzKGVudiksXG4gICAgICAgIHByZXZpZXc6IGVudiA9PT0gJ2RldmVsb3BtZW50JyxcbiAgICAgICAgLy8gPGh0dHBzOi8vZ2l0aHViLmNvbS9sYWRqcy9pMThuPlxuICAgICAgICAvLyBzZXQgdG8gYW4gb2JqZWN0IHRvIGNvbmZpZ3VyZSBhbmQgZW5hYmxlIGl0XG4gICAgICAgIGkxOG46IGZhbHNlLFxuICAgICAgICAvLyBwYXNzIGEgY3VzdG9tIHJlbmRlciBmdW5jdGlvbiBpZiBuZWNlc3NhcnlcbiAgICAgICAgcmVuZGVyOiB0aGlzLnJlbmRlci5iaW5kKHRoaXMpLFxuICAgICAgICBjdXN0b21SZW5kZXI6IGZhbHNlLFxuICAgICAgICAvLyBmb3JjZSB0ZXh0LW9ubHkgcmVuZGVyaW5nIG9mIHRlbXBsYXRlIChkaXNyZWdhcmRzIHRlbXBsYXRlIGZvbGRlcilcbiAgICAgICAgdGV4dE9ubHk6IGZhbHNlLFxuICAgICAgICAvLyA8aHR0cHM6Ly9naXRodWIuY29tL3dlcms4NS9ub2RlLWh0bWwtdG8tdGV4dD5cbiAgICAgICAgaHRtbFRvVGV4dDoge1xuICAgICAgICAgIGlnbm9yZUltYWdlOiB0cnVlXG4gICAgICAgIH0sXG4gICAgICAgIHN1YmplY3RQcmVmaXg6IGZhbHNlLFxuICAgICAgICAvLyA8aHR0cHM6Ly9naXRodWIuY29tL0F1dG9tYXR0aWMvanVpY2U+XG4gICAgICAgIGp1aWNlOiB0cnVlLFxuICAgICAgICBqdWljZVJlc291cmNlczoge1xuICAgICAgICAgIHByZXNlcnZlSW1wb3J0YW50OiB0cnVlLFxuICAgICAgICAgIHdlYlJlc291cmNlczoge1xuICAgICAgICAgICAgcmVsYXRpdmVUbzogcGF0aC5yZXNvbHZlKCdidWlsZCcpLFxuICAgICAgICAgICAgaW1hZ2VzOiBmYWxzZVxuICAgICAgICAgIH1cbiAgICAgICAgfSxcbiAgICAgICAgLy8gcGFzcyBhIHRyYW5zcG9ydCBjb25maWd1cmF0aW9uIG9iamVjdCBvciBhIHRyYW5zcG9ydCBpbnN0YW5jZVxuICAgICAgICAvLyAoZS5nLiBhbiBpbnN0YW5jZSBpcyBjcmVhdGVkIHZpYSBgbm9kZW1haWxlci5jcmVhdGVUcmFuc3BvcnRgKVxuICAgICAgICAvLyA8aHR0cHM6Ly9ub2RlbWFpbGVyLmNvbS90cmFuc3BvcnRzLz5cbiAgICAgICAgdHJhbnNwb3J0OiB7fVxuICAgICAgfSxcbiAgICAgIGNvbmZpZ1xuICAgICk7XG5cbiAgICAvLyBvdmVycmlkZSBleGlzdGluZyBtZXRob2RcbiAgICB0aGlzLnJlbmRlciA9IHRoaXMuY29uZmlnLnJlbmRlcjtcblxuICAgIGlmICghXy5pc0Z1bmN0aW9uKHRoaXMuY29uZmlnLnRyYW5zcG9ydC5zZW5kTWFpbCkpXG4gICAgICB0aGlzLmNvbmZpZy50cmFuc3BvcnQgPSBub2RlbWFpbGVyLmNyZWF0ZVRyYW5zcG9ydCh0aGlzLmNvbmZpZy50cmFuc3BvcnQpO1xuXG4gICAgZGVidWcoJ3RyYW5zZm9ybWVkIGNvbmZpZyAlTycsIHRoaXMuY29uZmlnKTtcblxuICAgIGF1dG9CaW5kKHRoaXMpO1xuICB9XG5cbiAgLy8gc2hvcnRoYW5kIHVzZSBvZiBganVpY2VSZXNvdXJjZXNgIHdpdGggdGhlIGNvbmZpZ1xuICAvLyAobWFpbmx5IGZvciBjdXN0b20gcmVuZGVycyBsaWtlIGZyb20gYSBkYXRhYmFzZSlcbiAganVpY2VSZXNvdXJjZXMoaHRtbCkge1xuICAgIHJldHVybiBqdWljZVJlc291cmNlcyhodG1sLCB0aGlzLmNvbmZpZy5qdWljZVJlc291cmNlcyk7XG4gIH1cblxuICAvLyBhIHNpbXBsZSBoZWxwZXIgZnVuY3Rpb24gdGhhdCBnZXRzIHRoZSBhY3R1YWwgZmlsZSBwYXRoIGZvciB0aGUgdGVtcGxhdGVcbiAgYXN5bmMgZ2V0VGVtcGxhdGVQYXRoKHRlbXBsYXRlKSB7XG4gICAgY29uc3QgW3Jvb3QsIHZpZXddID0gcGF0aC5pc0Fic29sdXRlKHRlbXBsYXRlKVxuICAgICAgPyBbcGF0aC5kaXJuYW1lKHRlbXBsYXRlKSwgcGF0aC5iYXNlbmFtZSh0ZW1wbGF0ZSldXG4gICAgICA6IFt0aGlzLmNvbmZpZy52aWV3cy5yb290LCB0ZW1wbGF0ZV07XG4gICAgY29uc3QgcGF0aHMgPSBhd2FpdCBnZXRQYXRocyhcbiAgICAgIHJvb3QsXG4gICAgICB2aWV3LFxuICAgICAgdGhpcy5jb25maWcudmlld3Mub3B0aW9ucy5leHRlbnNpb25cbiAgICApO1xuICAgIGNvbnN0IGZpbGVQYXRoID0gcGF0aC5yZXNvbHZlKHJvb3QsIHBhdGhzLnJlbCk7XG4gICAgcmV0dXJuIHsgZmlsZVBhdGgsIHBhdGhzIH07XG4gIH1cblxuICAvLyByZXR1cm5zIHRydWUgb3IgZmFsc2UgaWYgYSB0ZW1wbGF0ZSBleGlzdHNcbiAgLy8gKHVzZXMgc2FtZSBsb29rLXVwIGFwcHJvYWNoIGFzIGByZW5kZXJgIGZ1bmN0aW9uKVxuICBhc3luYyB0ZW1wbGF0ZUV4aXN0cyh2aWV3KSB7XG4gICAgdHJ5IHtcbiAgICAgIGNvbnN0IHsgZmlsZVBhdGggfSA9IGF3YWl0IHRoaXMuZ2V0VGVtcGxhdGVQYXRoKHZpZXcpO1xuICAgICAgY29uc3Qgc3RhdHMgPSBhd2FpdCBzdGF0KGZpbGVQYXRoKTtcbiAgICAgIGlmICghc3RhdHMuaXNGaWxlKCkpIHRocm93IG5ldyBFcnJvcihgJHtmaWxlUGF0aH0gd2FzIG5vdCBhIGZpbGVgKTtcbiAgICAgIHJldHVybiB0cnVlO1xuICAgIH0gY2F0Y2ggKGVycikge1xuICAgICAgZGVidWcoJ3RlbXBsYXRlRXhpc3RzJywgZXJyKTtcbiAgICAgIHJldHVybiBmYWxzZTtcbiAgICB9XG4gIH1cblxuICAvLyBwcm9taXNlIHZlcnNpb24gb2YgY29uc29saWRhdGUncyByZW5kZXJcbiAgLy8gaW5zcGlyZWQgYnkga29hLXZpZXdzIGFuZCByZS11c2VzIHRoZSBzYW1lIGNvbmZpZ1xuICAvLyA8aHR0cHM6Ly9naXRodWIuY29tL3F1ZWNrZXp6L2tvYS12aWV3cz5cbiAgYXN5bmMgcmVuZGVyKHZpZXcsIGxvY2FscyA9IHt9KSB7XG4gICAgY29uc3QgeyBtYXAsIGVuZ2luZVNvdXJjZSB9ID0gdGhpcy5jb25maWcudmlld3Mub3B0aW9ucztcbiAgICBjb25zdCB7IGZpbGVQYXRoLCBwYXRocyB9ID0gYXdhaXQgdGhpcy5nZXRUZW1wbGF0ZVBhdGgodmlldyk7XG4gICAgaWYgKHBhdGhzLmV4dCA9PT0gJ2h0bWwnICYmICFtYXApIHtcbiAgICAgIGNvbnN0IHJlcyA9IGF3YWl0IHJlYWRGaWxlKGZpbGVQYXRoLCAndXRmOCcpO1xuICAgICAgcmV0dXJuIHJlcztcbiAgICB9XG5cbiAgICBjb25zdCBlbmdpbmVOYW1lID0gbWFwICYmIG1hcFtwYXRocy5leHRdID8gbWFwW3BhdGhzLmV4dF0gOiBwYXRocy5leHQ7XG4gICAgY29uc3QgcmVuZGVyRm4gPSBlbmdpbmVTb3VyY2VbZW5naW5lTmFtZV07XG4gICAgaWYgKCFlbmdpbmVOYW1lIHx8ICFyZW5kZXJGbilcbiAgICAgIHRocm93IG5ldyBFcnJvcihcbiAgICAgICAgYEVuZ2luZSBub3QgZm91bmQgZm9yIHRoZSBcIi4ke3BhdGhzLmV4dH1cIiBmaWxlIGV4dGVuc2lvbmBcbiAgICAgICk7XG5cbiAgICBpZiAoXy5pc09iamVjdCh0aGlzLmNvbmZpZy5pMThuKSkge1xuICAgICAgY29uc3QgaTE4biA9IG5ldyBJMThOKFxuICAgICAgICBPYmplY3QuYXNzaWduKHt9LCB0aGlzLmNvbmZpZy5pMThuLCB7XG4gICAgICAgICAgcmVnaXN0ZXI6IGxvY2Fsc1xuICAgICAgICB9KVxuICAgICAgKTtcblxuICAgICAgLy8gc3VwcG9ydCBgbG9jYWxzLnVzZXIubGFzdF9sb2NhbGVgXG4gICAgICAvLyAoZS5nLiBmb3IgPGh0dHBzOi8vbGFkLmpzLm9yZz4pXG4gICAgICBpZiAoXy5pc09iamVjdChsb2NhbHMudXNlcikgJiYgXy5pc1N0cmluZyhsb2NhbHMudXNlci5sYXN0X2xvY2FsZSkpXG4gICAgICAgIGxvY2Fscy5sb2NhbGUgPSBsb2NhbHMudXNlci5sYXN0X2xvY2FsZTtcblxuICAgICAgaWYgKF8uaXNTdHJpbmcobG9jYWxzLmxvY2FsZSkpIGkxOG4uc2V0TG9jYWxlKGxvY2Fscy5sb2NhbGUpO1xuICAgIH1cblxuICAgIGNvbnN0IHJlcyA9IGF3YWl0IFByb21pc2UucHJvbWlzaWZ5KHJlbmRlckZuKShmaWxlUGF0aCwgbG9jYWxzKTtcbiAgICAvLyB0cmFuc2Zvcm0gdGhlIGh0bWwgd2l0aCBqdWljZSB1c2luZyByZW1vdGUgcGF0aHNcbiAgICAvLyBnb29nbGUgbm93IHN1cHBvcnRzIG1lZGlhIHF1ZXJpZXNcbiAgICAvLyBodHRwczovL2RldmVsb3BlcnMuZ29vZ2xlLmNvbS9nbWFpbC9kZXNpZ24vcmVmZXJlbmNlL3N1cHBvcnRlZF9jc3NcbiAgICBpZiAoIXRoaXMuY29uZmlnLmp1aWNlKSByZXR1cm4gcmVzO1xuICAgIGNvbnN0IGh0bWwgPSBhd2FpdCB0aGlzLmp1aWNlUmVzb3VyY2VzKHJlcyk7XG4gICAgcmV0dXJuIGh0bWw7XG4gIH1cblxuICAvLyBUT0RPOiB0aGlzIG5lZWRzIHJlZmFjdG9yZWRcbiAgLy8gc28gdGhhdCB3ZSByZW5kZXIgdGVtcGxhdGVzIGFzeW5jaHJvbm91c2x5XG4gIGFzeW5jIHJlbmRlckFsbCh0ZW1wbGF0ZSwgbG9jYWxzID0ge30sIG1lc3NhZ2UgPSB7fSkge1xuICAgIGxldCBzdWJqZWN0VGVtcGxhdGVFeGlzdHMgPSB0aGlzLmNvbmZpZy5jdXN0b21SZW5kZXI7XG4gICAgbGV0IGh0bWxUZW1wbGF0ZUV4aXN0cyA9IHRoaXMuY29uZmlnLmN1c3RvbVJlbmRlcjtcbiAgICBsZXQgdGV4dFRlbXBsYXRlRXhpc3RzID0gdGhpcy5jb25maWcuY3VzdG9tUmVuZGVyO1xuXG4gICAgaWYgKHRlbXBsYXRlICYmICF0aGlzLmNvbmZpZy5jdXN0b21SZW5kZXIpXG4gICAgICBbXG4gICAgICAgIHN1YmplY3RUZW1wbGF0ZUV4aXN0cyxcbiAgICAgICAgaHRtbFRlbXBsYXRlRXhpc3RzLFxuICAgICAgICB0ZXh0VGVtcGxhdGVFeGlzdHNcbiAgICAgIF0gPSBhd2FpdCBQcm9taXNlLmFsbChbXG4gICAgICAgIHRoaXMudGVtcGxhdGVFeGlzdHMoYCR7dGVtcGxhdGV9L3N1YmplY3RgKSxcbiAgICAgICAgdGhpcy50ZW1wbGF0ZUV4aXN0cyhgJHt0ZW1wbGF0ZX0vaHRtbGApLFxuICAgICAgICB0aGlzLnRlbXBsYXRlRXhpc3RzKGAke3RlbXBsYXRlfS90ZXh0YClcbiAgICAgIF0pO1xuXG4gICAgaWYgKCFtZXNzYWdlLnN1YmplY3QgJiYgc3ViamVjdFRlbXBsYXRlRXhpc3RzKSB7XG4gICAgICBtZXNzYWdlLnN1YmplY3QgPSBhd2FpdCB0aGlzLnJlbmRlcihcbiAgICAgICAgYCR7dGVtcGxhdGV9L3N1YmplY3RgLFxuICAgICAgICBPYmplY3QuYXNzaWduKHt9LCBsb2NhbHMsIHsgcHJldHR5OiBmYWxzZSB9KVxuICAgICAgKTtcbiAgICAgIG1lc3NhZ2Uuc3ViamVjdCA9IG1lc3NhZ2Uuc3ViamVjdC50cmltKCk7XG4gICAgfVxuXG4gICAgaWYgKG1lc3NhZ2Uuc3ViamVjdCAmJiB0aGlzLmNvbmZpZy5zdWJqZWN0UHJlZml4KVxuICAgICAgbWVzc2FnZS5zdWJqZWN0ID0gdGhpcy5jb25maWcuc3ViamVjdFByZWZpeCArIG1lc3NhZ2Uuc3ViamVjdDtcblxuICAgIGlmICghbWVzc2FnZS5odG1sICYmIGh0bWxUZW1wbGF0ZUV4aXN0cylcbiAgICAgIG1lc3NhZ2UuaHRtbCA9IGF3YWl0IHRoaXMucmVuZGVyKGAke3RlbXBsYXRlfS9odG1sYCwgbG9jYWxzKTtcblxuICAgIGlmICghbWVzc2FnZS50ZXh0ICYmIHRleHRUZW1wbGF0ZUV4aXN0cylcbiAgICAgIG1lc3NhZ2UudGV4dCA9IGF3YWl0IHRoaXMucmVuZGVyKFxuICAgICAgICBgJHt0ZW1wbGF0ZX0vdGV4dGAsXG4gICAgICAgIE9iamVjdC5hc3NpZ24oe30sIGxvY2FscywgeyBwcmV0dHk6IGZhbHNlIH0pXG4gICAgICApO1xuXG4gICAgaWYgKHRoaXMuY29uZmlnLmh0bWxUb1RleHQgJiYgbWVzc2FnZS5odG1sICYmICFtZXNzYWdlLnRleHQpXG4gICAgICAvLyB3ZSdkIHVzZSBub2RlbWFpbGVyLWh0bWwtdG8tdGV4dCBwbHVnaW5cbiAgICAgIC8vIGJ1dCB3ZSByZWFsbHkgZG9uJ3QgbmVlZCB0byBzdXBwb3J0IGNpZFxuICAgICAgLy8gPGh0dHBzOi8vZ2l0aHViLmNvbS9hbmRyaXM5L25vZGVtYWlsZXItaHRtbC10by10ZXh0PlxuICAgICAgbWVzc2FnZS50ZXh0ID0gaHRtbFRvVGV4dC5mcm9tU3RyaW5nKFxuICAgICAgICBtZXNzYWdlLmh0bWwsXG4gICAgICAgIHRoaXMuY29uZmlnLmh0bWxUb1RleHRcbiAgICAgICk7XG5cbiAgICAvLyBpZiB3ZSBvbmx5IHdhbnQgYSB0ZXh0LWJhc2VkIHZlcnNpb24gb2YgdGhlIGVtYWlsXG4gICAgaWYgKHRoaXMuY29uZmlnLnRleHRPbmx5KSBkZWxldGUgbWVzc2FnZS5odG1sO1xuXG4gICAgLy8gaWYgbm8gc3ViamVjdCwgaHRtbCwgb3IgdGV4dCBjb250ZW50IGV4aXN0cyB0aGVuIHdlIHNob3VsZFxuICAgIC8vIHRocm93IGFuIGVycm9yIHRoYXQgc2F5cyBhdCBsZWFzdCBvbmUgbXVzdCBiZSBmb3VuZFxuICAgIC8vIG90aGVyd2lzZSB0aGUgZW1haWwgd291bGQgYmUgYmxhbmsgKGRlZmVhdHMgcHVycG9zZSBvZiBlbWFpbC10ZW1wbGF0ZXMpXG4gICAgaWYgKFxuICAgICAgcy5pc0JsYW5rKG1lc3NhZ2Uuc3ViamVjdCkgJiZcbiAgICAgIHMuaXNCbGFuayhtZXNzYWdlLnRleHQpICYmXG4gICAgICBzLmlzQmxhbmsobWVzc2FnZS5odG1sKSAmJlxuICAgICAgXy5pc0FycmF5KG1lc3NhZ2UuYXR0YWNobWVudHMpICYmXG4gICAgICBfLmlzRW1wdHkobWVzc2FnZS5hdHRhY2htZW50cylcbiAgICApXG4gICAgICB0aHJvdyBuZXcgRXJyb3IoXG4gICAgICAgIGBObyBjb250ZW50IHdhcyBwYXNzZWQgZm9yIHN1YmplY3QsIGh0bWwsIHRleHQsIG5vciBhdHRhY2htZW50cyBtZXNzYWdlIHByb3BzLiBDaGVjayB0aGF0IHRoZSBmaWxlcyBmb3IgdGhlIHRlbXBsYXRlIFwiJHt0ZW1wbGF0ZX1cIiBleGlzdC5gXG4gICAgICApO1xuXG4gICAgcmV0dXJuIG1lc3NhZ2U7XG4gIH1cblxuICBhc3luYyBzZW5kKG9wdGlvbnMgPSB7fSkge1xuICAgIG9wdGlvbnMgPSBPYmplY3QuYXNzaWduKFxuICAgICAge1xuICAgICAgICB0ZW1wbGF0ZTogJycsXG4gICAgICAgIG1lc3NhZ2U6IHt9LFxuICAgICAgICBsb2NhbHM6IHt9XG4gICAgICB9LFxuICAgICAgb3B0aW9uc1xuICAgICk7XG5cbiAgICBsZXQgeyB0ZW1wbGF0ZSwgbWVzc2FnZSwgbG9jYWxzIH0gPSBvcHRpb25zO1xuXG4gICAgY29uc3QgYXR0YWNobWVudHMgPVxuICAgICAgbWVzc2FnZS5hdHRhY2htZW50cyB8fCB0aGlzLmNvbmZpZy5tZXNzYWdlLmF0dGFjaG1lbnRzIHx8IFtdO1xuXG4gICAgbWVzc2FnZSA9IF8uZGVmYXVsdHNEZWVwKFxuICAgICAge30sXG4gICAgICBfLm9taXQobWVzc2FnZSwgJ2F0dGFjaG1lbnRzJyksXG4gICAgICBfLm9taXQodGhpcy5jb25maWcubWVzc2FnZSwgJ2F0dGFjaG1lbnRzJylcbiAgICApO1xuICAgIGxvY2FscyA9IF8uZGVmYXVsdHNEZWVwKHt9LCB0aGlzLmNvbmZpZy52aWV3cy5sb2NhbHMsIGxvY2Fscyk7XG5cbiAgICBpZiAoYXR0YWNobWVudHMpIG1lc3NhZ2UuYXR0YWNobWVudHMgPSBhdHRhY2htZW50cztcblxuICAgIGRlYnVnKCd0ZW1wbGF0ZSAlcycsIHRlbXBsYXRlKTtcbiAgICBkZWJ1ZygnbWVzc2FnZSAlTycsIG1lc3NhZ2UpO1xuICAgIGRlYnVnKCdsb2NhbHMgKGtleXMgb25seSk6ICVPJywgT2JqZWN0LmtleXMobG9jYWxzKSk7XG5cbiAgICAvLyBnZXQgYWxsIGF2YWlsYWJsZSB0ZW1wbGF0ZXNcbiAgICBjb25zdCBvYmogPSBhd2FpdCB0aGlzLnJlbmRlckFsbCh0ZW1wbGF0ZSwgbG9jYWxzLCBtZXNzYWdlKTtcblxuICAgIC8vIGFzc2lnbiB0aGUgb2JqZWN0IHZhcmlhYmxlcyBvdmVyIHRvIHRoZSBtZXNzYWdlXG4gICAgT2JqZWN0LmFzc2lnbihtZXNzYWdlLCBvYmopO1xuXG4gICAgaWYgKHRoaXMuY29uZmlnLnByZXZpZXcpIHtcbiAgICAgIGRlYnVnKCd1c2luZyBgcHJldmlldy1lbWFpbGAgdG8gcHJldmlldyBlbWFpbCcpO1xuICAgICAgaWYgKF8uaXNPYmplY3QodGhpcy5jb25maWcucHJldmlldykpXG4gICAgICAgIGF3YWl0IHByZXZpZXdFbWFpbChtZXNzYWdlLCBudWxsLCB0cnVlLCB0aGlzLmNvbmZpZy5wcmV2aWV3KTtcbiAgICAgIGVsc2UgYXdhaXQgcHJldmlld0VtYWlsKG1lc3NhZ2UpO1xuICAgIH1cblxuICAgIGlmICghdGhpcy5jb25maWcuc2VuZCkge1xuICAgICAgZGVidWcoJ3NlbmQgZGlzYWJsZWQgc28gd2UgYXJlIGVuc3VyaW5nIEpTT05UcmFuc3BvcnQnKTtcbiAgICAgIC8vIDxodHRwczovL2dpdGh1Yi5jb20vbm9kZW1haWxlci9ub2RlbWFpbGVyL2lzc3Vlcy83OTg+XG4gICAgICAvLyBpZiAodGhpcy5jb25maWcudHJhbnNwb3J0Lm5hbWUgIT09ICdKU09OVHJhbnNwb3J0JylcbiAgICAgIHRoaXMuY29uZmlnLnRyYW5zcG9ydCA9IG5vZGVtYWlsZXIuY3JlYXRlVHJhbnNwb3J0KHtcbiAgICAgICAganNvblRyYW5zcG9ydDogdHJ1ZVxuICAgICAgfSk7XG4gICAgfVxuXG4gICAgY29uc3QgcmVzID0gYXdhaXQgdGhpcy5jb25maWcudHJhbnNwb3J0LnNlbmRNYWlsKG1lc3NhZ2UpO1xuICAgIGRlYnVnKCdtZXNzYWdlIHNlbnQnKTtcbiAgICByZXMub3JpZ2luYWxNZXNzYWdlID0gbWVzc2FnZTtcbiAgICByZXR1cm4gcmVzO1xuICB9XG59XG5cbm1vZHVsZS5leHBvcnRzID0gRW1haWw7XG4iXX0=