<script setup lang="ts">
import '../main.ts';
import i18n from '@/i18n';
import { provide } from 'vue';
import { I18nInjectionKey } from 'vue-i18n';

provide(I18nInjectionKey, i18n);
</script>

<template>
  <param-etab-ce id="app"></param-etab-ce>
</template>
