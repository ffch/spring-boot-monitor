var contextPath = 'actuator/';
getActuatorPath();

function getActuatorPath() {
	$.get("context", function(result){
		contextPath = result;
		console.log(contextPath);
	});
}