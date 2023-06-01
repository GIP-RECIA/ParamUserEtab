<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from "vue";
import "./assets/list.css";
import "./assets/detailList.css";
import "cropperjs/dist/cropper.css";
import DetailEtab from "./components/DetailEtab.vue";
import Etab from "./components/Etab.vue";
</script>

<template>
  <Etab></Etab>
</template>
