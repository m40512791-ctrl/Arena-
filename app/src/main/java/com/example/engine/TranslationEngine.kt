package com.example.engine

import com.example.model.AppLanguage

object TranslationEngine {

    fun translateStoryChapter(text: String, targetLanguage: AppLanguage): String {
        return when (targetLanguage) {
            AppLanguage.ENGLISH -> text
            AppLanguage.BENGALI -> translateToBengali(text)
            AppLanguage.HINDI -> translateToHindi(text)
            AppLanguage.JAPANESE -> translateToJapanese(text)
            AppLanguage.KOREAN -> translateToKorean(text)
            AppLanguage.CHINESE -> translateToChinese(text)
        }
    }

    private fun translateToBengali(text: String): String {
        return text
            .replace("The fabric of reality tore in a silent detonation of light and void.", "আলো এবং শূন্যতার এক নিস্তব্ধ বিস্ফোরণে বাস্তবতার বুনন ছিন্নভিন্ন হয়ে গেল।")
            .replace("Two solitary figures materialized from opposite ends of the multiverse", "মাল্টিভার্সের বিপরীত প্রান্ত থেকে দুটি নিঃসঙ্গ চরিত্র আবির্ভূত হলো")
            .replace("In the grand arena of Death Battle", "ডেথ ব্যাটেল অ্যারেনার এই মহাযুদ্ধে")
            .replace("The initial breach occurred in less than a Planck instant.", "প্ল্যাঙ্ক মুহূর্তের চেয়েও কম সময়ে প্রথম সংঘর্ষটি শুরু হলো।")
            .replace("Every parry cracked the local continuum.", "প্রতিটি প্রতিঘাতে স্থানীয় স্থান-কালের ধারাবাহিকতায় ফাটল ধরল।")
            .replace("The battlefield groaned. Laws of thermodynamics, causality, and dimensional stability began to unravel.", "যুদ্ধক্ষেত্রটি কেঁপে উঠল। তাপগতিবিদ্যা, কার্যকারণ তত্ত্ব এবং মাত্রাগত স্থিতিশীলতার নিয়মাবলী ভেঙে পড়তে শুরু করল।")
            .replace("This was the crux of the war—The Turning Point.", "এটিই ছিল যুদ্ধের মূল কেন্দ্রবিন্দু—টার্নিং পয়েন্ট বা পরিবর্তনের মুহূর্ত।")
            .replace("With no remaining retreat, the final clash ignited.", "আর কোনো পিছু হটার উপায় না থাকায় চূড়ান্ত সংঘর্ষ শুরু হলো।")
            .replace("The dust of shattered dimensions settled over the", "ধ্বংসপ্রাপ্ত মাত্রার ধূলিকণা শান্ত হয়ে নেমে এলো")
            .replace("Chapter 1: The Beginning", "অধ্যায় ১: সূচনা")
            .replace("Chapter 2: First Encounter", "অধ্যায় ২: প্রথম সংঘর্ষ")
            .replace("Chapter 3: Power Awakening", "অধ্যায় ৩: ক্ষমতার জাগরণ")
            .replace("Chapter 4: The Turning Point", "অধ্যায় ৪: টার্নিং পয়েন্ট")
            .replace("Chapter 5: Final Battle", "অধ্যায় ৫: চূড়ান্ত যুদ্ধ")
            .replace("Chapter 6: The Aftermath", "অধ্যায় ৬: পরিণাম ও সমাপ্তি")
            .replace("Round 1: Initial Encounter & Probe", "রাউন্ড ১: প্রাথমিক মুখোমুখি ও পর্যবেক্ষণ")
            .replace("Round 2: Ability Showcase & Hax Clashes", "রাউন্ড ২: ক্ষমতার প্রদর্শন ও হ্যাক্স লড়াই")
            .replace("Round 3: Power Escalation & Peak Forms", "রাউন্ড ৩: ক্ষমতার বিস্ফোরণ ও শীর্ষ রূপ")
            .replace("Round 4: Final Clash & Decisive Verdict", "রাউন্ড ৪: চূড়ান্ত আঘাত ও চূড়ান্ত রায়")
    }

    private fun translateToHindi(text: String): String {
        return text
            .replace("The fabric of reality tore in a silent detonation of light and void.", "प्रकाश और शून्यता के एक शांत विस्फोट में वास्तविकता का ताना-बाना बिखर गया।")
            .replace("Two solitary figures materialized from opposite ends of the multiverse", "मल्टीवर्स के विपरीत सिरों से दो योद्धा प्रकट हुए")
            .replace("In the grand arena of Death Battle", "डेथ बैटल अखाड़े के इस भव्य युद्ध में")
            .replace("The initial breach occurred in less than a Planck instant.", "एक प्लैंक क्षण से भी कम समय में पहला प्रहार हुआ।")
            .replace("Every parry cracked the local continuum.", "हर बचाव ने स्थानीय समय-स्थान सातत्य को हिलाकर रख दिया।")
            .replace("Chapter 1: The Beginning", "अध्याय 1: प्रारंभ")
            .replace("Chapter 2: First Encounter", "अध्याय 2: पहली मुठभेड़")
            .replace("Chapter 3: Power Awakening", "अध्याय 3: शक्ति का जागरण")
            .replace("Chapter 4: The Turning Point", "अध्याय 4: महत्वपूर्ण मोड़")
            .replace("Chapter 5: Final Battle", "अध्याय 5: अंतिम महायुद्ध")
            .replace("Chapter 6: The Aftermath", "अध्याय 6: परिणाम और विजय")
    }

    private fun translateToJapanese(text: String): String {
        return text
            .replace("The fabric of reality tore in a silent detonation of light and void.", "光と虚無の静寂な爆発の中で、現実の織物が引き裂かれた。")
            .replace("Two solitary figures materialized from opposite ends of the multiverse", "多元宇宙の両極から二つの孤高の戦士が具現化した。")
            .replace("Chapter 1: The Beginning", "第1章：始まり")
            .replace("Chapter 2: First Encounter", "第2章：初会戦")
            .replace("Chapter 3: Power Awakening", "第3章：力の覚醒")
            .replace("Chapter 4: The Turning Point", "第4章：転換点")
            .replace("Chapter 5: Final Battle", "第5章：最終決戦")
            .replace("Chapter 6: The Aftermath", "第6章：結末と余波")
    }

    private fun translateToKorean(text: String): String {
        return text
            .replace("The fabric of reality tore in a silent detonation of light and void.", "빛과 공허의 고요한 폭발 속에서 현실의 차원이 찢어졌다.")
            .replace("Chapter 1: The Beginning", "제1장: 시작")
            .replace("Chapter 2: First Encounter", "제2장: 첫 조우")
            .replace("Chapter 3: Power Awakening", "제3장: 힘의 각성")
            .replace("Chapter 4: The Turning Point", "제4장: 전환점")
            .replace("Chapter 5: Final Battle", "제5장: 최종 결전")
            .replace("Chapter 6: The Aftermath", "제6장: 여파와 승리")
    }

    private fun translateToChinese(text: String): String {
        return text
            .replace("The fabric of reality tore in a silent detonation of light and void.", "在光芒与虚无的寂静爆裂中，现实的织锦被彻底撕裂。")
            .replace("Chapter 1: The Beginning", "第一章：开端")
            .replace("Chapter 2: First Encounter", "第二章：初次交锋")
            .replace("Chapter 3: Power Awakening", "第三章：力量觉醒")
            .replace("Chapter 4: The Turning Point", "第四章：转折之刻")
            .replace("Chapter 5: Final Battle", "第五章：最终决战")
            .replace("Chapter 6: The Aftermath", "第六章：终章与余波")
    }
}
