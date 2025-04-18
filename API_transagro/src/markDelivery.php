<?php
// markDelivery.php
require_once 'config.php';

if (isset($_POST['deliveryId'])) {
    $deliveryId = intval($_POST['deliveryId']);
    $stmt = $pdo->prepare("UPDATE deliveries SET delivered = 1 WHERE id = :deliveryId");
    if ($stmt->execute(['deliveryId' => $deliveryId])) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false]);
    }
} else {
    echo json_encode(["error" => "Paramètre deliveryId manquant"]);
}
