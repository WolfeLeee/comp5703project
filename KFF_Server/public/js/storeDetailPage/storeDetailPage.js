$(document).ready(function()
    {
        /**
         * Function to check whether the submit and reset button css visiblity property is hidden;
         * If it is true, the visibility value will be changed to visible
         */

        function showButton(){
            if($(".infodisplay_settings__submitbutton").css('visibility') == 'hidden'){
                $(".infodisplay_settings__submitbutton").css('visibility','visible');
            }
            if($(".infodisplay_settings__resetbutton").css('visibility') == 'hidden'){
                $(".infodisplay_settings__resetbutton").css('visibility','visible');
            }
        }


        /** function to make the editable class turns into a textarea
         * When the admin double clicks on the span element which contains the editable div
         * The system will check whether the textarea Element ".editBox" is available
         * If it is true, nothing will happen.
         * Otherwise, a textarea element with class name equals ".editBox" will be pushed to the editable div element
         */
        $('.pagebody_mainbody__infodisplay__span').dblclick(function(){
            if($(this).find('.infodisplay_settings_editable').first().find('.editBox').length == 0){
                var editable = $(this).find('.infodisplay_settings_editable').first().html();
                var editbox = '<textarea class="editBox">' + editable + '</textarea>';
                $(this).find('.infodisplay_settings_editable').first().html(editbox);
                showButton();
            }
        })

        /** function to make the editable class turns into a textarea
         * When the admin clicks on the edit text
         * The system will check whether the textarea Element ".editBox" is available
         * If it is true, nothing will happen.
         * Otherwise, a textarea element with class name equals ".editBox" will be pushed to the editable div element
         */

        $('.infodisplay_settings_edittext').click(function(){
            var parent = $(this).parent();
            if(parent.find('.infodisplay_settings_editable').first().find('.editBox').length == 0){
                var editable = parent.find('.infodisplay_settings_editable').first().html();
                var editbox = '<textarea class="editBox">' + editable + '</textarea>';
                parent.find('.infodisplay_settings_editable').first().html(editbox);
                showButton();
            }
        })

        /** When user clicks enter in the textarea ".editBox",
         * The textarea will disappear; Hence, the value of the editable div will be replaced by the textarea value
         */

        $('.infodisplay_settings_editable').on("keypress", function(event){
            if(event.which == 13){
                var $text = $(this).find('.editBox').first().val();
                $(this).html($text);
            }
        })

        /** When user clicks submit button, all the information regarding that brand
         * in the div box will be collected and sent to the server to update that Brand
         */

        $('.infodisplay_settings__submitbutton').on("click",function(e) {
            var Store_Element = $('.infodisplay_settings_editable');
            var store_name = Store_Element.get(0).textContent;
            var store_id = $(this).attr('title');
            var jqxhr = $.get("/GetAllStore")
                .done(function(data){
                    IdenticalStoreName(data,store_name,store_id);
                })
                .fail(function(jqXHR) {
                    console.log(jqXHR.status);
                })
        })

        function IdenticalStoreName(data,store_name,store_id,event)
        {
            var identicalstorename = false
            for (var i = 0 ; i< data.length ; i++)
            {
                if(new String(data[i]._id).valueOf() !== new String(store_id).valueOf())
                {
                    if(new String(data[i].storeName).toLowerCase().valueOf() == new String(store_name).toLowerCase().valueOf())
                    {
                        identicalstorename = true;
                    }
                }
            }
            if(identicalstorename)
            {
                $('.pagebody_mainbody__infodisplay__span__errorlog').empty();
                $('.pagebody_mainbody__infodisplay__span__errorlog').append("<div class='.infodisplay_settings_item'>"+"This name has already been used;"+"</div><br>");
                if($('.editBox').length > 0)
                {
                    $('.pagebody_mainbody__infodisplay__span__errorlog').append("<div class='.infodisplay_settings_item'>"+"Please finish editing first;"+"</div><br>");
                }
            }
            else if ($('.editBox').length > 0)
            {
                $('.pagebody_mainbody__infodisplay__span__errorlog').empty();
                $('.pagebody_mainbody__infodisplay__span__errorlog').append("<div class='.infodisplay_settings_item'>"+"Please finish editing first;"+"</div><br>");
            }
            else
            {
                $('.pagebody_mainbody__infodisplay__span__errorlog').empty();
                var confirmation = confirm("Do you want to update this store");
                if(confirmation == true)
                {
                    var params = {
                        store_id:store_id,
                        store_name:store_name
                    };
                    post('/detailstorePage_updateBrandSummary',params,"post");
                }
                else
                {
                    event.preventDefault();
                }
            }
        }

        // var Store_Element = $('.infodisplay_settings_editable');
        // var store_name = Brand_Element.get(0).textContent;



        //     var confirmation = confirm("Do you want to update this brand?");
        //     if( confirmation == true){
        //
        //         var brand_category = Brand_Element.get(1).textContent;
        //         var brand_id = $(this).attr('title');
        //         if($('#infodisplay_settings_item__form__File')[0].files[0] == null)
        //         {
        //             var params = {
        //                 brand_name: brand_name,
        //                 brand_category: brand_category,
        //                 brand_id:brand_id
        //             };
        //             post('/detailproductPage_updateBrandSummary',params,'post');
        //         }
        //         else
        //         {
        //             var params = {
        //                 brand_name: brand_name,
        //                 brand_category: brand_category,
        //                 brand_id:brand_id,
        //             };
        //             var form = $('#infodisplay_settings_item__form');
        //             for(var key in params) {
        //                 if(params.hasOwnProperty(key)) {
        //                     var input = $("<input>").attr("type", "hidden").attr("name", key).val(params[key]);
        //                     $(form).append($(input));
        //                 }
        //             }
        //             $(form).submit();
        //             post('/detailproductPage_updateBrandSummary',params,'post',$('.inputform_brand__BrandImage')[0].files[0]);
        //         }
        //
        //     }
        //     else {
        //         e.preventDefault();
        //     }
        //
        // })

        $('.infodisplay_settings__resetbutton').on("click",function(){
            location.reload();
        })
        /** Post function
         *
         */
        function post(path, params, method,image) {
            method = method || "get"; // Set method to post by default if not specified.

            // The rest of this code assumes you are not using a library.
            // It can be made less wordy if you use one.
            var form = $(document.createElement('form'));
            $(form).attr("action", path);
            $(form).attr("method", method);
            $(form).attr("enctype","multipart/form-data");


            for(var key in params) {
                if(params.hasOwnProperty(key)) {

                    var input = $("<input>").attr("type", "hidden").attr("name", key).val(params[key]);
                    $(form).append($(input));

                }
            }
            if(image != null)
            {
                var input = $("<input>").attr("type", "file").attr("name", "Image").val(image);
                $(form).append($(input));
            }
            $(document.body).append(form);
            $(form).submit();
        }
    }
)