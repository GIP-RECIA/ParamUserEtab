<script setup lang="ts">
import '../main';
import i18n from '@/i18n';
import { provide } from 'vue';
import { I18nInjectionKey } from 'vue-i18n';

provide(I18nInjectionKey, i18n);

const props = defineProps<{
  baseApiUrl: string;
  paramEtabApi: string;
  userInfoApiUrl: string;
}>();

console.log('paramEtabApi: ', props.paramEtabApi);
</script>

<template>
  <param-etab-ce
    id="app"
    :base-api-url="baseApiUrl"
    :param-etab-api="paramEtabApi"
    :user-info-api-url="userInfoApiUrl"
  ></param-etab-ce>
</template>
