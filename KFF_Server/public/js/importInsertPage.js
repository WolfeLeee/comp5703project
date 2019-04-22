$(document).ready(function()
    {
        /**
         * Function which makes "pagebody_inputform__brand" and "pagebody_inputform__store appear
         * after changing the select element "inputform_choosedata__data"
         */
        $('#inputform_choosedata__data').on('change',function(){
            if($(this).val().localeCompare("Brand")==0)
            {
                $(this).parent().css("display","none");
                $('.pagebody_inputform__brand').css("display","block");
                $('.pagebody_mainbody__sectionheader__brand').html("Import/ Insert Brand Data");

            }
        })

        /**
         * Function to check identical brand name
         */
        var branddata = null;

        $('.pagebody_inputform__brandsubmit').on("click",function(event)
        {
            var brand_name = $('.pagebody_inputform__brandnameinput').first().val();

            var jqxhr = $.get( "/GetAllBrand")
                .done(function(data){
                        BrandCallback(data);
                    }
                )
                .fail(function(jqXHR) {
                    console.log(jqXHR.status);
                })
        })

        function BrandCallback(data)
        {
            branddata = data;

            var brand_name = $('.pagebody_inputform__brandnameinput').first().val();
            var brand_category = $('.pagebody_inputform__brandcategoryinput').first().val();
            var brand_accreditation = $('.pagebody_inputform__brandaccreditationinput').first().val();
            var brand_rating = $('.pagebody_inputform__brandratinginput').first().val();

            var identicalname = false;
            for(var i = 0 ; i < branddata.length ; i ++)
            {
                if(new String(branddata[i].Brand_Name.toLowerCase()).valueOf() == new String(brand_name.toLowerCase()).valueOf() && new String(branddata[i].Category.toLowerCase()).valueOf() == new String(brand_category.toLowerCase()).valueOf() )
                {
                    identicalname = true;
                }
            }
            if(new String("").valueOf() == new String(brand_name).valueOf() || new String("").valueOf() == new String(brand_category).valueOf() || new String("").valueOf() == new String(brand_accreditation).valueOf() || new String("").valueOf() == new String(brand_rating).valueOf())
            {
                $('.pagebody_inputform__errordialog').empty();
                $('.pagebody_inputform__errordialog').append("<b>"+"Please enter all the required information;"+"</b><br>");
                if(identicalname)
                {
                    $('.pagebody_inputform__errordialog').append("<b>"+"This name and category have already been used;"+"</b><br>");
                }
            }
            else if (identicalname)
            {
                $('.pagebody_inputform__errordialog').empty();
                $('.pagebody_inputform__errordialog').append("<b>"+"This name has already been used;"+"</b><br>");
            }
            else
            {
                $('.pagebody_inputform__errordialog').empty();
                $('.pagebody_inputform__inputnewbrand').children().eq(0).submit();
            }
        }


    })