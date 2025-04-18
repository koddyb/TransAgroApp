<?php
// login.php
require_once 'config.php';

if(isset($_POST['email']) && isset($_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];
    
    // Mise à jour de la requête pour inclure la colonne "role"
    $stmt = $pdo->prepare("SELECT id, name, email, role FROM drivers WHERE email = :email AND password = :password");
    $stmt->execute(['email' => $email, 'password' => $password]);
    $driver = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if($driver) {
        echo json_encode(["success" => true, "driver" => $driver]);
    } else {
        echo json_encode(["success" => false, "error" => "Identifiants invalides"]);
    }
} else {
    echo json_encode(["error" => "Paramètres manquants"]);
}
