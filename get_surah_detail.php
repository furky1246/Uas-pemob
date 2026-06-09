<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$chapterId = filter_input(INPUT_GET, 'chapter_id', FILTER_VALIDATE_INT);

if (!$chapterId || $chapterId < 1) {
    jsonResponse(['success' => false, 'message' => 'Parameter chapter_id tidak valid'], 400);
}

$db = getDB();

$stmtChapter = $db->prepare(
    'SELECT id, name, verse_count FROM chapters WHERE id = ? LIMIT 1'
);
$stmtChapter->execute([$chapterId]);
$chapter = $stmtChapter->fetch();

if (!$chapter) {
    jsonResponse(['success' => false, 'message' => 'Surah tidak ditemukan'], 404);
}

$stmtVerses = $db->prepare('
    SELECT
        v.number,
        v.text_uthmani,
        vt.text  AS tafsir,
        va.url   AS audio_url
    FROM verses v
    LEFT JOIN verse_tafsirs vt ON vt.id_verse = v.id
    LEFT JOIN verse_audios  va ON va.id_verse = v.id
    WHERE v.id_chapter = ?
    GROUP BY v.id
    ORDER BY v.number ASC
');
$stmtVerses->execute([$chapterId]);
$rows = $stmtVerses->fetchAll();

$verses = array_map(fn($row) => [
    'verse_number' => (int) $row['number'],
    'arabic'       => $row['text_uthmani'],
    'tafsir'       => $row['tafsir'],
    'audio_url'    => $row['audio_url'],
], $rows);

jsonResponse([
    'surah' => [
        'id'          => (int) $chapter['id'],
        'name'        => $chapter['name'],
        'verse_count' => (int) $chapter['verse_count'],
    ],
    'verses' => $verses,
]);
