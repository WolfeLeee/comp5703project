$(document).ready(function()
    {
        /**
         * Highlight the entire row when hover on the
         */
        $('.accreditationlist_table__Accreditation').mouseover(function(){
           var parentnode = $(this).parent();
           parentnode.stop().animate({'background-color':'rgba(15,15,255,0.25)'},400);
        }).mouseout(function(){
            var parentnode = $(this).parent();
            parentnode.stop().animate({'background-color':'transparent'},200);
        });

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
            var confirmation = confirm('Do you want to delete this(these) '+checkedboxes.length+' Accreditation(s)?');
            if(confirmation == true){
                var accrids = [];
                for(var i = 0 ; i < checkedboxes.length; i++)
                {
                    accrids.push(checkedboxes.get(i).getAttribute('value'));
                }
                var params = {
                    accrids: accrids,
                    productid : $(this).attr('value')
                };
                post('/detailproductPage_Accreditation__Delete',params,"get");
            }
            else {
                e.preventDefault();
            }

        })

        /** Function to transform the Accreditation and Rating table cell into textarea;
         * When the admin double clicks on a row,
         * The system will check whether the textarea ".EditBox" is available.
         * If it is true, nothing will happen;
         * Otherwise, two textarea elements will be pushed to the Accreditation and Rating cells.
         */

            $('.accreditationlist_table_tablerow').dblclick(function(){
               var editableaccr = $(this).children().eq(0);
               var editablerating = $(this).children().eq(1);
               if($(this).children().eq(0).find('.editBox').length == 0){
                   var editboxaccr = '<textarea class="editBox">' + editableaccr.html() + '</textarea>';
                   editableaccr.html(editboxaccr);
                   var editboxrating = '<input autocomplete="off" list="browsers" class="editBox" name="browser"><datalist id="browsers"><option selected value="Good"><option value="Best"><option value="Avoid"></datalist>'
                   editablerating.html(editboxrating);
               }
            });

        /** When user clicks enter in the textarea ".editBox",
         * The textarea will disappear; Hence, the value of the editable div will be replaced by the textarea value
         */

        $('.accreditationlist_table__Accreditation').on("keypress", function(event){
            if(event.which == 13){
                var editableaccr = $(this).parent().children().eq(0);
                var editablerating = $(this).parent().children().eq(1);
                var $text1 = editableaccr.find('.editBox').first().val();
                var $text2 = editablerating.find('.editBox').first().val();
                if($text2.localeCompare("") == 0){
                    alert("Please choose a rating");
                    event.preventDefault();
                    }
                else {
                    editableaccr.html($text1);
                    editablerating.html($text2);
                    var buttons = $(this).parent().find('.accreditationlist_table__Buttons').first();
                    if(buttons.css('display').localeCompare('none') == 0){
                        buttons.css('display','flex');
                    }
                }
            }
        })

        /** Listener function for the edit button
         * When the admin clicks on the edit button
         * The system will check whether the textarea ".EditBox" is available.
         * If it is true, nothing will happen;
         * Otherwise, two textarea elements will be pushed to the Accreditation and Rating cells.
         */
        $('.accreditationlist_table__Edit').on('click',function(){
            var editableaccr = $(this).parent().parent().children().eq(0);
            var editablerating = $(this).parent().parent().children().eq(1);
            if($(this).children().eq(0).find('.editBox').length == 0){
                var editboxaccr = '<textarea class="editBox">' + editableaccr.html() + '</textarea>';
                editableaccr.html(editboxaccr);
                var editboxrating = '<input autocomplete="off" list="browsers" class="editBox" name="browser"><datalist id="browsers"><option selected value="Good"><option value="Best"><option value="Avoid"></datalist>'
                editablerating.html(editboxrating);
            }
        })

        /** Confirm update button listener
         * When the admins click on the button, any changes that made to the Accreditation will be sent to the server
         */

        $('.accreditationlist_table__Confirm').on('click',function(){
            var accreditation = $(this).parent().parent().children().eq(0).html();
            var rating = $(this).parent().parent().children().eq(1).html()
            var productid = $(this).val();
            var accrid = $(this).attr('title');
            var params = {
                accreditation: accreditation,
                rating: rating,
                productid: productid,
                accrid: accrid
            }
            post('/detailproductPage_Accreditation__Update',params,'get');
        })


        /** Function to send new accreditation to the server
         * when the admin click the Add button in the modal
         */

        $(".modal-adding__Accreditation").on("click",function(event){
            var modalbody = $(this).parent().parent().children().eq(1);
            if(modalbody.children().eq(0).val().localeCompare("")==0 || modalbody.children().eq(1).val().localeCompare("")==0)
            {
                modalbody.children().eq(2).html("Please enter the Accreditation and the Rating you want to add to this brand");
            }
            else
            {
                params = {
                    accreditation: modalbody.children().eq(0).val(),
                    rating: modalbody.children().eq(1).val(),
                    productid: $(this).attr("value")
                }
                post('/detailproductPage_Accreditation__Insert',params,"get");
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