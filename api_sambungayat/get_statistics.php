<?php
// get_statistics.php
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
    // Query statistics
    $stmt = $pdo->prepare("SELECT total_score, best_streak, current_surah, highest_unlocked_surah FROM user_progress WHERE user_id = ?");
    $stmt->execute([$userId]);
    $stats = $stmt->fetch();

    if ($stats) {
        sendResponse([
            "total_score"            => (int)$stats['total_score'],
            "best_streak"            => (int)$stats['best_streak'],
            "current_surah"          => (int)$stats['current_surah'],
            "highest_unlocked_surah" => (int)$stats['highest_unlocked_surah']
        ]);
    } else {
        // Return defaults if no progress exists
        sendResponse([
            "total_score"            => 0,
            "best_streak"            => 0,
            "current_surah"          => 1,
            "highest_unlocked_surah" => 1
        ]);
    }
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
