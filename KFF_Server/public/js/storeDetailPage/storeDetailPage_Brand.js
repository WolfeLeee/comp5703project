$(document).ready(function()
    {
        $('.modal-body__search').on('input',function(){
            var brandnotinstore = $('.modal-body__store__brandnotinstorelist');
            for (var i = 0; i < brandnotinstore.length ; i++)
            {
               if($(brandnotinstore[i]).children().eq(0).text().toLowerCase().includes($(this).val().toLowerCase()))
               {
                   $(brandnotinstore[i]).css('display',"block");
               }
               else
               {
                   $(brandnotinstore[i]).css('display',"none");
               }
            }
        })

        $('.modal-body__store__insertbrandtostore').unbind().on("click",function()
        {
            var brandid = $(this).data('brandid');
            var storeid = $(this).data('storeid');
            var addressid = $(this).data('addressid');
            var jqxhr = $.get("/detailstorePage_Brand__Insert?brandid="+brandid+"&storeid="+storeid+"&addressid="+addressid)
                .done(function(data){
                    alert(data.matched);
                })
                .fail(function(jqXHR) {
                    console.log(jqXHR.status);
                })
        })

        /**
         * When a checkbox is checked the delete button will appear
         */
        $('.button_selectall').change(function(){
            if($(this).is(':checked')){
                $('.accreditationlist_table__Delete').prop('checked',true);
            }
            else {
                $('.accreditationlist_table__Delete').prop('checked',false);
            }
            makeappear();
        })


        $('.accreditationlist_table__Delete').change(function(){
            makeappear();
        })

        function makeappear(){
            var Ischeck = false;
            var Allcheck = false;
            var checkboxes = $('.accreditationlist_table__Delete:checked');
            if(checkboxes.length > 0){
                Ischeck = true;
                if(checkboxes.length == $('.accreditationlist_table__Delete').length){
                    Allcheck = true;
                }
            }
            if(Ischeck == true)
            {
                $('.deletebutton_deletemarked').css('visibility','visible');
            }
            else
            {
                $('.deletebutton_deletemarked').css('visibility','hidden');
            }
            if(Allcheck){
                $('.button_selectall').prop('checked',true);
            }
            else {
                $('.button_selectall').prop('checked',false);
            }
        }

        $('.deletebutton_deletemarked').on("click",function(e){
            var checkedboxes = $('.accreditationlist_table__Delete:checked');
            var confirmation = confirm('Do you want to delete this(these) '+checkedboxes.length+' brand(s) from the store?');
            if(confirmation == true){
                var brinstids = [];
                for(var i = 0 ; i < checkedboxes.length; i++)
                {
                    brinstids.push(checkedboxes.get(i).getAttribute('value'));
                }
                var params = {
                    brinstids: brinstids,
                    storeid : $(this).attr('value'),
                    addressid: $(this).data('addressid')
                };
                post('/detailstorePage_Brand__Delete',params,"get");
            }
            else {
                e.preventDefault();
            }

        })


        /** Search box listener
         * When the admin type in the search box and press "enter"/ or click the Search button
         *
         */

        $('.pagebody_mainbody__infodisplay__searchinput').on("keypress",function(event){
            if(event.which == 13){
                var params = {
                    storeid: $(this).attr('title'),
                    addressid: $(this).data('addressid'),
                    searchstring: $(this).val()
                }
                post('/detailstorePage_Brand',params,"get");
            }
        })

        $('.pagebody_mainbody_infodisplay__searchsubmitbutton').on('click',function(event){
            var params = {
                storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr('title'),
                addressid: $('.pagebody_mainbody__infodisplay__searchinput').first().data('addressid'),
                searchstring: $('.pagebody_mainbody__infodisplay__searchinput').first().val()
            }
            post('/detailstorePage_Brand',params,"get");
        })

        /**
         * paginationbutton listener
         */

        $('.pagination_button.page-item').on('click',function(event){
            var searchstring = $(this).attr('title');
            if($(this).children().eq(0).text().toLowerCase().localeCompare(("Next").toLowerCase()) == 0)
            {
                var page = parseInt($('.pagination>.active>a').text()) + 1;
                if(searchstring == null)
                {
                    var params = {
                        page : page,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Brand',params,"get");
                }
                else
                {
                    var params = {
                        page : page,
                        searchstring: searchstring,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Brand',params,"get");
                }
            }
            else if($(this).children().eq(0).text().toLowerCase().localeCompare(("Previous").toLowerCase()) == 0)
            {
                var page = parseInt($('.pagination>.active>a').text()) - 1;
                if(searchstring == null)
                {
                    var params = {
                        page : page,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Brand',params,"get");
                }
                else
                {
                    var params = {
                        page : page,
                        searchstring: searchstring,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Brand',params,"get");
                }
            }
            else
            {
                if(searchstring == null)
                {
                    var params = {
                        page : $(this).children().eq(0).text(),
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Brand',params,"get");
                }
                else
                {
                    var params = {
                        page : $(this).children().eq(0).text(),
                        searchstring: searchstring,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Brand',params,"get");
                }

            }
        })



        /** Post function
         *
         */
        function post(path, params, method) {
            method = method || "get"; // Set method to post by default if not specified.

            // The rest of this code assumes you are not using a library.
            // It can be made less wordy if you use one.
            var form = document.createElement("form");
            form.setAttribute("method", method);
            form.setAttribute("action", path);

            for(var key in params) {
                if(params.hasOwnProperty(key)) {
                    var hiddenField = document.createElement("input");
                    hiddenField.setAttribute("type", "hidden");
                    hiddenField.setAttribute("name", key);
                    hiddenField.setAttribute("value", params[key]);

                    form.appendChild(hiddenField);
                }
            }

            document.body.appendChild(form);
            form.submit();
        };

    }
)