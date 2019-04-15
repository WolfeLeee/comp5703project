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
            if(Allcheck){
                $('.button_selectall').prop('checked',true);
            }
            else {
                $('.button_selectall').prop('checked',false);
            }

        }

    }
)