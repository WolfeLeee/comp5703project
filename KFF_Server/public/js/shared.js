$(document).ready(function()
{
    // Shared function for all layouts to enable the search box on the top of the view to search for brand
    $("#pageheader_navigationbar__searchbox").on("keypress",function(event)
    {
        if(event.which == 13){
            var params = {
                searchstring: $(this).val()
            }
            post('/dbmanagement',params,"get");
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
})