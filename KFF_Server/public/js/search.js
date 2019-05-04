

$(document).ready(function()
{
	/* * * * * * * * * * * * * * * *
	 * Searching function in table *
	 * * * * * * * * * * * * * * * */

	// search button listener
	$("#searchButton").click(function(event)
	{
		event.preventDefault();

		// get the text from search
		var searchText = $("#searchInput").val().toString().toLowerCase();
		// $("#searchInput").val("");
		// console.log(displayAllData.length);

		// check if match to the search text with product data
		// and display the matched data in the table
		var tbody = document.getElementById("table_body");
		tbody.innerHTML = "";  // empty the original table
		var tr, td;
		var numOfRows = 0;
		var numOfSearchProducts = 0;
		for(var i = 0; i < displayAllData.length; i++)
		{
			var brandName = displayAllData[i].Brand_Name.toString().toLowerCase();
			if(brandName.startsWith(searchText))
			{
				tr = tbody.insertRow(numOfRows++);

				// brand name
				td = tr.insertCell(0);
				td.setAttribute("class", "Table_Brand_Name");
				td.innerHTML = "<a href='/detailproductPage?productid="+displayAllData[i]._id+"'>" + displayAllData[i].Brand_Name + "</a>";

				// accreditation
				td = tr.insertCell(1);
				for(var j = 0; j < displayAllData[i].Accreditation.length; j++)
				{
					td.innerHTML += "<div>" + displayAllData[i].Accreditation[j].Accreditation + " - " + displayAllData[i].Accreditation[j].Rating + "</div>";
				}

				// category
				td = tr.insertCell(2);
				td.innerHTML = displayAllData[i].Category;

				// delete checkbox
				td = tr.insertCell(3);
				td.setAttribute("class", "deletecheckbox");
				td.innerHTML = "<input type='checkbox' value='"+displayAllData[i]._id+"'>";

				// count the number of searched products
				numOfSearchProducts++;
			}
		}

		// change the number of display products
		document.getElementById("datable_info").innerHTML = "Showing " + numOfSearchProducts + " of " + displayAllData.length;
	});
	// enter will trigger the search button as well
	var searchInput = document.getElementById("searchInput");
	if(searchInput)
	{
		searchInput.addEventListener("keyup", function(event)
		{
			if(event.keyCode === 13)
			{
				event.preventDefault();
				$("#searchButton").click();
			}
		});
	}

	/* * * * * * * * * * * * * * * * *
	 * Displaying function in table  *
	 * * * * * * * * * * * * * * * * */

	$("select.custom-select").change(function()
	{
		// var selectedNumber = $(this).children("option:selected").text();
		location.href = $(this).val();
	});

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