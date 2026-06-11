<?php
// get_juz.php
require_once __DIR__ . '/config.php';

// Verify GET request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use GET."
    ], 405);
}

try {
    // Query all Juzs ordered by number
    $stmt = $pdo->query("SELECT id, name, number FROM juzs ORDER BY number ASC");
    $juzs = $stmt->fetchAll();

    $response = [];
    foreach ($juzs as $juz) {
        $response[] = [
            "id"     => (int)$juz['id'],
            "name"   => $juz['name'],
            "number" => (int)$juz['number']
        ];
    }

    sendResponse($response);
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
