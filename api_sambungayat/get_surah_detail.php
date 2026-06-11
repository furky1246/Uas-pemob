<?php
// get_surah_detail.php
require_once __DIR__ . '/config.php';

// Verify GET request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use GET."
    ], 405);
}

// Get chapter ID from query parameters
$chapterId = isset($_GET['chapter_id']) ? (int)$_GET['chapter_id'] : 0;

if ($chapterId <= 0 || $chapterId > 114) {
    sendResponse([
        "success" => false,
        "message" => "Chapter ID tidak valid. Harus antara 1 dan 114."
    ], 400);
}

try {
    // 1. Query Surah Info
    $stmtSurah = $pdo->prepare("SELECT id, name, verse_count FROM chapters WHERE id = ?");
    $stmtSurah->execute([$chapterId]);
    $surah = $stmtSurah->fetch();

    if (!$surah) {
        sendResponse([
            "success" => false,
            "message" => "Surah tidak ditemukan."
        ], 404);
    }

    // 2. Query Verses
    $stmtVerses = $pdo->prepare("
        SELECT 
            v.number AS verse_number, 
            v.text_uthmani AS arabic, 
            vt.text AS tafsir, 
            va.url AS audio_url 
        FROM verses v 
        LEFT JOIN verse_translations vt ON v.id = vt.id_verse AND vt.id_translation = 33 
        LEFT JOIN verse_audios va ON v.id = va.id_verse AND va.id_recitation = 1 
        WHERE v.id_chapter = ? 
        ORDER BY v.number ASC
    ");
    $stmtVerses->execute([$chapterId]);
    $versesData = $stmtVerses->fetchAll();

    $verses = [];
    foreach ($versesData as $verse) {
        $verses[] = [
            "verse_number" => (int)$verse['verse_number'],
            "arabic"       => $verse['arabic'],
            "tafsir"       => $verse['tafsir'],
            "audio_url"    => $verse['audio_url']
        ];
    }

    // 3. Assemble response matching SurahDetailResponse model
    sendResponse([
        "surah" => [
            "id"          => (int)$surah['id'],
            "name"        => $surah['name'],
            "verse_count" => (int)$surah['verse_count']
        ],
        "verses" => $verses
    ]);

} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
