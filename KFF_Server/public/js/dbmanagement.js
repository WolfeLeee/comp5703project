

$(document).ready(function()
{
	/* * * * * * * * * * * * * * * *
	 * Sort function in the table  *
	 * * * * * * * * * * * * * * * */

	$('.sorting').on("click",function(){
		var params = {
			sortquery: $(this).data('sort'),
			searchstring: $(this).data('search'),
			perPage: $(this).data('perpage')
		}
		post('/dbmanagement',params,"get");
	})


	/* * * * * * * * * * * * * * * * *
	 * Search function in the table  *
	 * * * * * * * * * * * * * * * * */

	// enter will trigger the search function
	var searchInput = document.getElementById("searchInput");
	if(searchInput)
	{
		searchInput.addEventListener("keyup", function(event)
		{
			if(event.keyCode === 13)
			{
				event.preventDefault();
				// $("#searchButton").click();
			}
		});
	};
	/* * * * * * * * * * * * * * * * * * * *
	 * Pagination to navigate through page *
	 * * * * * * * * * * * * * * * * * * * */

	$('.page-link').click(function(){
		if(new String($(this).text().toLowerCase()).valueOf() == new String("Previous").valueOf())
		{
			var searchstring = $(this).data("search") || "";
			var params = {
				perPage: $('.custom-select').children("option:selected").val(),
				page: parseInt($('.active').first().text()) - 1,
				searchstring: searchstring,
				sortquery: $(this).data('sort')
			}
			post('/dbmanagement',params,"get");
		}
		else if (new String($(this).text().toLowerCase()).valueOf() == new String("Next").valueOf())
		{
			var searchstring = $(this).data("search") || "";
			var params = {
				perPage: $('.custom-select').children("option:selected").val(),
				page: parseInt($('.active').first().text()) + 1,
				searchstring: searchstring,
				sortquery: $(this).data('sort')
			}
			post('/dbmanagement',params,"get");
		}
		else
		{
			var searchstring = $(this).data("search") || "";
			var params = {
				perPage: $('.custom-select').children("option:selected").val(),
				page: parseInt($(this).text()),
				searchstring: searchstring,
				sortquery: $(this).data('sort')
			}
			post('/dbmanagement',params,"get");
		}
	})

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Change number of products showed in a page function *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	$('.custom-select').on('change',function(){
		var params = {
			perPage: $(this).val(),
			searchstring: $(this).data("searchstring")
		}
		post('/dbmanagement',params,"get");
	})

	/* * * * * * * * * * * * * * * * *
	 * Search function in the table  *
	 * * * * * * * * * * * * * * * * */

	$('#searchInput').keypress(function(e)
		{
			if(e.which == 13)
			{
				var params = {
					searchstring: $(this).val()
				}
				post('/dbmanagement',params,"get");
			}
		}
	)

	$('#searchButton').on("click",function(){
		var params = {
			searchstring: $('#searchInput').val()
		}
		post('/dbmanagement',params,"get");
	})

	/* * * * * * * * * * * * * * * * *
	 * Delete function in the table  *
	 * * * * * * * * * * * * * * * * */

	$('.button_selectall').change(function(){
		if($(this).is(':checked')){
			$('.accreditationlist_table__Delete').prop('checked',true);
		}
		else {
			$('.accreditationlist_table__Delete').prop('checked',false);
		}
		makeappear();
	});


	$('.accreditationlist_table__Delete').change(function(){
		makeappear();
	});

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
	};

	$('.deletebutton_deletemarked').on("click",function(e){
		var checkedboxes = $('.accreditationlist_table__Delete:checked');
		var confirmation = confirm('Do you want to delete this(these) '+checkedboxes.length+' Brand(s)?');
		if(confirmation == true){
			var brids = [];
			for(var i = 0 ; i < checkedboxes.length; i++)
			{
				brids.push(checkedboxes.get(i).getAttribute('value'));
			}
			var params = {
				brids: brids,
			};
			post('/dbmanagement_Delete',params,"get");
		}
		else {
			e.preventDefault();
		}

	});

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


});