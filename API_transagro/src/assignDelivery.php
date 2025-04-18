<?php
// assignDelivery.php
require_once 'config.php';

if(isset($_POST['driverId']) && isset($_POST['deliveryId'])) {
    $driverId = intval($_POST['driverId']);
    $deliveryId = intval($_POST['deliveryId']);

    // Requête d'update : affecte la livraison au chauffeur
    $stmt = $pdo->prepare("UPDATE deliveries SET driver_id = :driverId WHERE id = :deliveryId");
    if($stmt->execute(['driverId' => $driverId, 'deliveryId' => $deliveryId])) {
        echo json_encode(["success" => true]);
    } else {
        // Si la requête n'exécute pas correctement, renvoyer un message d'erreur
        echo json_encode(["success" => false, "error" => "L'assignation a échoué."]);
    }
} else {
    echo json_encode(["error" => "Paramètres manquants"]);
}
