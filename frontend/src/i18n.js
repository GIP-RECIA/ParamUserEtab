import { createI18n } from "vue-i18n";

import en from "./locales/en.json";
import fr from "./locales/fr.json";


function getBrowserLang(availableLanguages) {

    const browserLanguagePropertyKeys = [
        "languages",
        "language",
        "browserLanguage",
        "userLanguage",
        "systemLanguage"
    ];

    const allLangs = browserLanguagePropertyKeys
        // merge all values into flattened array
        .flatMap((key) => navigator[key])
        // Remove undefined values
        .filter((v) => v)
        // Shorten strings to use two chars (en-US -> en)
        .map((v) => v.substring(0,2))
        // Returns unique values
        .filter((v, i, a) => a.indexOf(v) === i);

    // Returns first language matched in available languages 
    const detectedLocale = allLangs.find((x) => availableLanguages.includes(x));
    //console.warn("getBrowserLang : ",allLangs, detectedLocale)

    // If no local is detected, fallback to 'en'
    return detectedLocale || "en";
}

function getPageLang(availableLanguages) {
    // retrieve lang from html lang tag
    const pageLang = document.documentElement.lang;
    if (pageLang) {
        const allLangs = [pageLang, pageLang.substring(0,2)];

        const detectedLocale = allLangs.find((x) => availableLanguages.includes(x));
     
        return detectedLocale || "en";
    }
    return getBrowserLang(availableLanguages);
}


export default createI18n({
    legacy: false,
    globalInjection: true,
    locale: getPageLang(["fr-FR", "fr", "en-US", "en"]),
    allowComposition: true,
    fallbackLocale: "en",
    messages: {
        en,
        fr
    }
});