<?php
// get_progress.php
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
    // Query progress
    $stmt = $pdo->prepare("SELECT total_score, best_streak, highest_unlocked_surah, current_surah, current_verse FROM user_progress WHERE user_id = ?");
    $stmt->execute([$userId]);
    $progress = $stmt->fetch();

    if ($progress) {
        sendResponse([
            "total_score"            => (int)$progress['total_score'],
            "best_streak"            => (int)$progress['best_streak'],
            "highest_unlocked_surah" => (int)$progress['highest_unlocked_surah'],
            "current_surah"          => (int)$progress['current_surah'],
            "current_verse"          => (int)$progress['current_verse']
        ]);
    } else {
        // Fallback to default if not found
        sendResponse([
            "total_score"            => 0,
            "best_streak"            => 0,
            "highest_unlocked_surah" => 1,
            "current_surah"          => 1,
            "current_verse"          => 1
        ]);
    }
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
