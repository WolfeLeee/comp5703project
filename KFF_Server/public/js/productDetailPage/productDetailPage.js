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

        $('.infodisplay_settings__submitbutton').on("click",function(){
            var Brand_Element = $('.infodisplay_settings_editable');
            var brand_name = Brand_Element.get(0).textContent;
            var brand_category = Brand_Element.get(1).textContent;
            var brand_id = $(this).attr('title');
            var params = {
                brand_name: brand_name,
                brand_category: brand_category,
                brand_id:brand_id
            };
            post('/detailproductPage_updateBrandSummary',params,'get');
        })

        $('.infodisplay_settings__resetbutton').on("click",function(){
            location.reload();
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
        }





    }
)