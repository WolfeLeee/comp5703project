window.onload = function()
{
    var modal = document.getElementById('modal-wrapper');
    var modalReset = document.getElementById('modal-reset');
    //var modalRegister = document.getElementById('modal-register');
    window.onclick = function(event)
    {
        if (event.target === modal)
        {
            modal.style.display = "none";
        }
        if (event.target === modalReset)
        {
            modalReset.style.display = "none";
        }
        // if (event.target === modalRegister)
        // {
        //     modalRegister.style.display = "none";
        // }
    };
};

// $(document).ready(function()
// {
//     var url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles=Germany&formatversion=2&rvprop=timestamp%7Cuser&rvstart=2018-02-24T13%3A30%3A17.000Z";
//     $.post('/test', {url: url}, function(resData)
//     {
//         console.log(resData);
//         console.log(resData.revisions.length);
//     });
// });
