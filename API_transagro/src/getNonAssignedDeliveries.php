<?php
// getNonAssignedDeliveries.php
require_once 'config.php';

$stmt = $pdo->prepare("SELECT id, address, latitude, longitude, delivered, ville FROM deliveries WHERE driver_id IS NULL");
$stmt->execute();
$deliveries = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Conversion du champ 'delivered' en booléen pour chaque livraison
foreach ($deliveries as &$delivery) {
    $delivery['delivered'] = (bool)$delivery['delivered'];
}
echo json_encode($deliveries);
