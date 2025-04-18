<?php
// getDeliveries.php
require_once 'config.php';

if(isset($_GET['driverId'])) {
    $driverId = intval($_GET['driverId']);
    $stmt = $pdo->prepare("SELECT id, address, latitude, longitude, delivered, ville FROM deliveries WHERE driver_id = :driverId");
    $stmt->execute(['driverId' => $driverId]);
    $deliveries = $stmt->fetchAll(PDO::FETCH_ASSOC);
    // (Optionnel) Conversion du champ delivered en booléen
    foreach ($deliveries as &$delivery) {
        $delivery['delivered'] = (bool)$delivery['delivered'];
    }
    echo json_encode($deliveries);
} else {
    echo json_encode(["error" => "Paramètre driverId manquant"]);
}
