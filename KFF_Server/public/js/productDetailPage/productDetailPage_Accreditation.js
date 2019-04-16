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

        $('.deletebutton_deletemarked').on("click",function(){
            var checkedboxes = $('.accreditationlist_table__Delete:checked');
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
        })



        // $('.deletebutton_deletemarked').click(function(){
        //     var checkedboxes = $('.accreditationlist_table__Delete:checked');
        //     var accrids = [];
        //     for(var i = 0 ; i < checkedboxes.length; i++)
        //     {
        //         accrposition.push(checkedboxes.get(i).getAttribute('value'));
        //     }
        //     var params = {
        //         accrids: accrids,
        //         brandid : $(this).getAttribute('value');
        //     }
        //     post('/detailproductPage_Accreditation__Delete',params,"get");
        //
        // });

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