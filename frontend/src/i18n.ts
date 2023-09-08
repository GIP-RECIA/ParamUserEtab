import en from './locales/en.json';
import fr from './locales/fr.json';
import { createI18n } from 'vue-i18n';

function getBrowserLang(availableLanguages: Array<string>): string {
  // These window.navigator contain language information
  // 1. languages -> Array of preferred languages
  //                 (eg ["en-US", "zh-CN", "ja-JP"]) Firefox^32, Chrome^32
  // 2. language  -> Preferred language as String
  //                 (eg "en-US") Firefox^5, IE^11, Safari,
  //                 Chrome sends Browser UI language
  // 3. browserLanguage -> UI Language of IE
  // 4. userLanguage    -> Language of Windows Regional Options
  // 5. systemLanguage  -> UI Language of Windows
  const browserLanguagePropertyKeys: Array<string> = [
    'languages',
    'language',
    'browserLanguage',
    'userLanguage',
    'systemLanguage',
  ];

  const allLangs: Array<string> = browserLanguagePropertyKeys
    // @ts-ignore
    // merge all values into flattened array
    .flatMap((key: string) => navigator[key])
    // Remove undefined values
    .filter((v: string) => v)
    // Shorten strings to use two chars (en-US -> en)
    .map((v: string) => v.substring(0, 2))
    // Returns unique values
    .filter((v: any, i: any, a: any) => a.indexOf(v) === i);

  // Returns first language matched in available languages
  const detectedLocale: string | undefined = allLangs.find((x) => availableLanguages.includes(x));

  // If no locale is detected, fallback to 'en'
  return detectedLocale || 'en';
}

function getPageLang(availableLanguages: Array<string>): string {
  // retrieve lang from html lang tag
  const pageLang = document.documentElement.lang;
  if (pageLang) {
    // we add the the shorten two char lang in the list in case the more specific lang isn't provided
    const allLangs = [pageLang, pageLang.substring(0, 2)];
    // Returns first language matched in available languages
    const detectedLocale: string | undefined = allLangs.find((x) => availableLanguages.includes(x));
    // If no available language is detected, fallback to 'en'
    return detectedLocale || 'en';
  }
  // if no lang is retrieved from the document page we try to resolve from the browser
  return getBrowserLang(availableLanguages);
}

export default createI18n<false>({
  legacy: false,
  globalInjection: true,
  locale: getPageLang(['fr-FR', 'fr', 'en-US', 'en']),
  allowComposition: true,
  fallbackLocale: 'en',
  messages: {
    en,
    fr,
  },
});
