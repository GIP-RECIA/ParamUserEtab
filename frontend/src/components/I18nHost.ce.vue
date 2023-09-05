<script setup lang="ts">
import i18n from "@/i18n";
import { provide } from "vue";
import { I18nInjectionKey } from "vue-i18n";
import "../main.ts";
// import "../assets/list.css";
// import "../assets/detailList.css";
//import "cropperjs/dist/cropper.css";

provide(I18nInjectionKey, i18n);
</script>

<template>
  <div>
    <etab-ce id="components"></etab-ce>
  </div>
</template>
<style>
@import "../assets/base.css";

#components {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2rem;
  font-weight: normal;
}

a,
.green {
  text-decoration: none;
  color: hsla(160, 100%, 37%, 1);
  transition: 0.4s;
}

@media (hover: hover) {
  a:hover {
    background-color: hsla(160, 100%, 37%, 0.2);
  }
}

@media (min-width: 1024px) {
  body {
    display: flex;
    place-items: center;
  }

  #components {
    display: flex;
    grid-template-columns: 1fr 1fr;
    padding: 0 2rem;
  }
}

@media (max-width: 1024px) {
  #components {
    display: flex;
    flex-wrap: wrap;
  }
}
</style>
