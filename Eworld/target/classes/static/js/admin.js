let flag = false;
function enableStatus(id){
	if(flag == false){
		event.preventDefault();
   		$("#status"+id).prop("disabled", false);
   		$("#delivery"+id).prop("disabled", false);
   		$("#btn"+id).prop("disabled", false);
   		$("#"+id).text("CANCEL");
   		flag = true;
	}
	else{
		event.preventDefault();
   		$("#status"+id).prop("disabled", true);
   		$("#delivery"+id).prop("disabled", true);
   		$("#btn"+id).prop("disabled", true);
   		$("#"+id).text("EDIT");
   		flag = false;
	}
}