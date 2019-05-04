$(document).ready(function()
{
    /**
     * Function which makes "pagebody_inputform__brand" and "pagebody_inputform__store appear
     * after changing the select element "inputform_choosedata__data"
     */
    $('#inputform_choosedata__data').on('change',function()
    {
        if($(this).val().localeCompare("Brand") == 0)
        {
            $(this).parent().css("display","none");
            $('.pagebody_inputform__brand').css("display","block");
            $('.pagebody_mainbody__sectionheader__brand').html("Publish/ Release Brand Data");
        }
        else if ($(this).val().localeCompare("Store") == 0)
        {
            $(this).parent().css("display","none");
            $('.pagebody_inputform__store').css("display","block");
            $('.pagebody_mainbody__sectionheader__brand').html("Publish/ Release Store Data");
        }
    });



});