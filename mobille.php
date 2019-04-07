<!DOCTYPE html>
<html>
<head>
<title>GO CON</title>
<div>
<h1 style="background-color: #008577; color: white"><center>GO CON</center></h1>
</div>

<script src="https://www.gstatic.com/firebasejs/5.9.3/firebase.js"></script>
<script>
  // Initialize Firebase
  var config = {
    apiKey: "AIzaSyC02JIScESCospbD9u9yvjp4NCz3VbFi0w",
    authDomain: "gocon-2f45b.firebaseapp.com",
    databaseURL: "https://gocon-2f45b.firebaseio.com",
    projectId: "gocon-2f45b",
    storageBucket: "gocon-2f45b.appspot.com",
    messagingSenderId: "950023838753"
  };
  firebase.initializeApp(config);
</script>
</head>

<br><br>
<body style="background-image: linear-gradient(to right,#00b09b,#96c93d) ">
	<center>
  <div style="border-radius: 10px; border: 2px solid black;width: 800px;height: 300px; margin: 5px; padding: 5px; background-image: linear-gradient(to left,#1f4037,#99f2c8) ">
  	<img src="GoConLogo.jpg " style="width: 150px;">
	<form action="mobile.php" method="post">
		<br>
		<input name="mnumber" type="text"  id="num" style="font-size: 20px; width: 300px;height: 40px; border-radius: 10px; text-align: center;"placeholder="Enter Your Mobile No." required/> <br><br>
		<input name="fun" type="submit" style="width: 150px;height: 45px; background-color:; color: black; border-radius: 10px; background-image:linear-gradient(to left,rgb(236,239,240),rgb(156,173,149)) font-size:100px" value="PROCEED"/>
	</form>
   </div>
</center>
<center>
<h1><b><i>Users List</i></b></h1>
<!div style="border-radius: 10px; border: 2px solid black;width: 400px; margin: 5px; padding: 5px;">
<table id="tb1_users_list" border="3" style="border-radius: 5px; border-spacing: 20px;" >
	<tr>
		<td style="font-size: 25px;"><b>From</b></td>
		<td style="font-size: 25px;"><b>Message</b></td>
	</tr>
</table>
<!/div>
</center>
<script >
	var tblUsers=document.getElementById('tb1_users_list');
	var databaseRef=firebase.database().ref('messsages/');
	var rowIndex=1;

	databaseRef.once('value', function(snapshot) {
  	  snapshot.forEach(function(childSnapshot) {
       var childKey = childSnapshot.key;
        var childData = childSnapshot.val();
        var row=tblUsers.insertRow(rowIndex);
        var cellId=row.insertCell(0);
        var cellName=row.insertCell(1);
        cellId.appendChild(document.createTextNode(childKey));
        cellName.appendChild(document.createTextNode(childData.user_name));
        rowIndex=rowIndex+1;

       });
     });
	
	function save_user()
	{
		var user_name=document.getElementById('user_name').value;
 		

		var uid=firebase.database().ref().child('users').push().key;
		var data={
			user_id:uid,
			user_name:user_name
		}
		var updates={};
		updates['/users/'+ uid]=data;
		firebase.database().ref().update(updates);
		alert('The User is created SuccessFully');
		reload_page();
}
	function update_user()
	{
		var user_name=document.getElementById('user_name').value;
		var user_id=document.getElementById('user_id').value;
		var data={
			user_id:user_id,
			user_name:user_name
		}
		var updates={};
		updates['/users/'+ user_id]=data;
		firebase.database().ref().update(updates);
		alert('The User is updated Succesfully');
		reload_page();
	}
	function delete_user()
	{
		var user_id=document.getElementById('user_id').value;
		firebase.database().ref().child('/users/' + user_id).remove();
		alert('The User is Deleted');
		reload_page(); 

	}
	function reload_page()
	{
		window.location.reload()

	}
</script>
	
</body>
</html>
