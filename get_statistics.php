<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$userId = filter_input(INPUT_GET, 'user_id', FILTER_VALIDATE_INT);

if (!$userId || $userId < 1) {
    jsonResponse(['success' => false, 'message' => 'Parameter user_id tidak valid'], 400);
}

$db   = getDB();
$stmt = $db->prepare('
    SELECT total_score, best_streak, current_surah, highest_unlocked_surah
    FROM user_progress
    WHERE user_id = ?
    LIMIT 1
');
$stmt->execute([$userId]);
$row = $stmt->fetch();

if (!$row) {
    jsonResponse([
        'total_score'            => 0,
        'best_streak'            => 0,
        'current_surah'          => 1,
        'highest_unlocked_surah' => 1,
    ]);
}

jsonResponse([
    'total_score'            => (int) $row['total_score'],
    'best_streak'            => (int) $row['best_streak'],
    'current_surah'          => (int) $row['current_surah'],
    'highest_unlocked_surah' => (int) $row['highest_unlocked_surah'],
]);
