<?php  
 //load_data.php  
 $connect = mysqli_connect("localhost", "root", "", "mobile");  
 $output = '';  
 if(isset($_POST["cnumber"]))  
 {  
      if($_POST["cnumber"] != '')  
      {  
           $sql = "SELECT * FROM user WHERE cnumber= '".$_POST["cnumber"]."'";  
      }  
      else  
      {  
           $sql = "SELECT * FROM user";  
      }  
      $result = mysqli_query($connect, $sql);  
      while($row = mysqli_fetch_array($result))  
      {  
           $output .= '<div class="col-md-3"><div style="border:1px solid #ccc; padding:20px; margin-bottom:20px;">'.$row["unumber"].'</div></div>';  
      }  
      echo $output;  
 }  
 ?>  
