<?php
// getDriversByCity.php
require_once 'config.php';

if(isset($_GET['ville'])) {
    $ville = $_GET['ville'];
    $stmt = $pdo->prepare("SELECT id, name, email, role, ville FROM drivers WHERE ville = :ville AND role = 'driver'");
    $stmt->execute(['ville' => $ville]);
    $drivers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($drivers);
} else {
    echo json_encode(["error" => "Paramètre ville manquant"]);
}
