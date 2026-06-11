<?php
// seed.php
require_once __DIR__ . '/config.php';

// Disable timeout limits for long download processes
set_time_limit(300);

// Set HTML or text formatting
$isCli = (php_sapi_name() === 'cli');
if (!$isCli) {
    echo "<pre>";
}

echo "==================================================\n";
echo "QURAN DATABASE SEEDER FOR db_quran_emufassir\n";
echo "==================================================\n";

try {
    // 1. Verify Chapters table has data
    $stmt = $pdo->query("SELECT COUNT(*) FROM chapters");
    $chapterCount = $stmt->fetchColumn();
    echo "Chapters in database: $chapterCount\n";

    // 2. Fetch and Seed Verses for Surah 1 (Al-Fatihah) and Surah 78-114 (Juz 30)
    $stmt = $pdo->query("SELECT COUNT(*) FROM verses");
    $verseCount = $stmt->fetchColumn();

    if ($verseCount == 0) {
        $surahsToFetch = array_merge([1], range(78, 114));
        echo "Fetching and inserting verses for " . count($surahsToFetch) . " Surahs (Al-Fatihah + Juz 30) into new schema...\n";
        
        $stmtInsertVerse = $pdo->prepare("
            INSERT INTO verses (id_chapter, number, text_uthmani, text_uthmani_simple, transliteration, id_juz) 
            VALUES (?, ?, ?, ?, ?, ?)
        ");
        
        $stmtInsertTranslation = $pdo->prepare("
            INSERT INTO verse_translations (text, id_translation, id_verse) 
            VALUES (?, 33, ?)
        ");
        
        $stmtInsertAudio = $pdo->prepare("
            INSERT INTO verse_audios (url, id_verse, id_recitation) 
            VALUES (?, ?, 1)
        ");
        
        foreach ($surahsToFetch as $sNum) {
            echo "Fetching Surah $sNum... ";
            
            $url = "https://api.alquran.cloud/v1/surah/{$sNum}/editions/quran-simple,id.indonesian,ar.alafasy";
            $opts = [
                "http" => [
                    "method" => "GET",
                    "header" => "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)\r\n"
                ]
            ];
            $context = stream_context_create($opts);
            $response = file_get_contents($url, false, $context);
            
            if ($response === false) {
                echo "FAILED!\n";
                continue;
            }
            
            $resData = json_decode($response, true);
            if (!isset($resData['data'])) {
                echo "INVALID FORMAT!\n";
                continue;
            }
            
            $editions = $resData['data'];
            $arabicEdition = $editions[0]['ayahs'];
            $translationEdition = $editions[1]['ayahs'];
            $audioEdition = $editions[2]['ayahs'];
            
            $count = count($arabicEdition);
            echo "Found $count verses. Inserting... ";
            
            $pdo->beginTransaction();
            for ($i = 0; $i < $count; $i++) {
                $vNum = (int)$arabicEdition[$i]['numberInSurah'];
                $arabicText = $arabicEdition[$i]['text'];
                $translationText = $translationEdition[$i]['text'];
                $audioUrl = $audioEdition[$i]['text'];
                $juzId = (int)$arabicEdition[$i]['juz'];
                
                // Remove Bismillah prefix from simple text for game compatibility
                $simpleText = str_replace("بسم الله الرحمن الرحيم", "", $arabicText);
                if (empty(trim($simpleText))) {
                    $simpleText = $arabicText;
                }
                
                // 1. Insert into verses table
                $stmtInsertVerse->execute([
                    $sNum,
                    $vNum,
                    $arabicText,
                    trim($simpleText),
                    'verse_' . $sNum . '_' . $vNum, // placeholder transliteration
                    $juzId
                ]);
                $verseId = (int)$pdo->lastInsertId();
                
                // 2. Insert into verse_translations (ID 33 is Indonesian)
                $stmtInsertTranslation->execute([
                    $translationText,
                    $verseId
                ]);
                
                // 3. Insert into verse_audios (ID 1 is Recitation 1)
                $stmtInsertAudio->execute([
                    $audioUrl,
                    $verseId
                ]);
            }
            $pdo->commit();
            echo "DONE.\n";
        }
        echo "\nVerses, Translations, and Audios tables seeded successfully!\n";
    } else {
        echo "Verses table already has $verseCount verses. Skipping verses seeding.\n";
    }

    echo "\n==================================================\n";
    echo "SEEDING PROCESS COMPLETED!\n";
    echo "==================================================\n";

} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo "\nERROR: " . $e->getMessage() . "\n";
}

if (!$isCli) {
    echo "</pre>";
}
