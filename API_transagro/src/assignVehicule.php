<?php
// assignVehicle.php
require_once 'config.php';

if (isset($_POST['driverId']) && isset($_POST['vehicleId'])) {
    $driverId = intval($_POST['driverId']);
    $vehicleId = intval($_POST['vehicleId']);

    $stmt = $pdo->prepare("INSERT INTO driver_vehicles (driver_id, vehicle_id) VALUES (:driverId, :vehicleId)");
    if ($stmt->execute(['driverId' => $driverId, 'vehicleId' => $vehicleId])) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => "L'assignation du véhicule a échoué"]);
    }
} else {
    echo json_encode(["error" => "Paramètres manquants"]);
}
