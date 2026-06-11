<?php
// get_profile.php
require_once __DIR__ . '/config.php';

// Verify GET request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use GET."
    ], 405);
}

// Get user ID from query parameters
$userId = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;

if ($userId <= 0) {
    sendResponse([
        "success" => false,
        "message" => "User ID tidak valid atau kosong."
    ], 400);
}

try {
    // Query user profile
    $stmt = $pdo->prepare("SELECT id, name, email FROM users WHERE id = ?");
    $stmt->execute([$userId]);
    $user = $stmt->fetch();

    if ($user) {
        // Return profile info matching ProfileResponse model
        sendResponse([
            "id" => (int)$user['id'],
            "name" => $user['name'],
            "email" => $user['email']
        ]);
    } else {
        sendResponse([
            "success" => false,
            "message" => "User tidak ditemukan."
        ], 404);
    }
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
