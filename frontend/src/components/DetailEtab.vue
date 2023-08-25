<script setup lang="ts">
import { ref, watchEffect, computed } from "vue";
import ImageCropper from "./ImageCropper.vue";
import axios from "axios";
import Swal from "sweetalert2";
import type { StructureDetail } from "../types/structureType";

import { useI18n } from "vue-i18n";

const { t } = useI18n();
const m = (key: string): string => t(`detail-etab.${key}`);
const details = ref<StructureDetail>({});
// const details = ref<any[]>([]);

const props = defineProps<{
  detail: string;
}>();

watchEffect((): void => {
  void (async () => {
    await fetchDetailData(props.detail);
  })();
});

async function fetchDetailData(id: string) {
  if (id != "") {
    try {
      const response = await axios.get(`/parametab/${id}`);
      details.value = response.data;
    } catch (error) {
      console.error("error: ", error);
    }
  }
}

const handleUpdated = async ({ urlEtab }) => {
  const res = await axios.get(urlEtab);
  details.value = res.data;
};

async function updateInfo() {
  console.warn(details.value);
  const dataJson = `/parametab/updateV2/${props.detail}`;

  axios
    .put(dataJson, details.value)
    .then(async (response) => {
      Swal.fire({
        title: "Sauvegardé",
        icon: "success",
      });
    })
    .catch(function (error) {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "Something went wrong!",
      });
    });
}

const isButtonDisabled = computed(() => {
  return (
    (details.value.structCustomDisplayName === null ||
      details.value.structCustomDisplayName === "") &&
    (details.value.structSiteWeb === null || details.value.structSiteWeb === "")
  );
});
</script>

<template>
  <span class="warn">
    {{ m("warn") }}
    <br />{{ m("explication") }}
  </span>
  <div class="title-info">{{ m("info") }}</div>
  <div class="container">
    <ImageCropper
      :detail-etab="details"
      :image-url="details.structLogo"
      :id-etab="details.id"
      @updated="handleUpdated"
    />

    <div class="infos">
      <label class="label">
        <input class="input-field" type="text" :value="details.name" disabled />
        <span>{{ m("nom-institutionnel") }}</span>
      </label>
      <label class="label">
        <input
          id="customName"
          class="input-field"
          type="text"
          :placeholder="m('nom-personnalise-placeholder')"
          v-model="details.structCustomDisplayName"
        />
        <span>{{ m("nom-personnalise-titre") }}</span>
      </label>
      <label class="label">
        <input
          class="input-field"
          type="text"
          :placeholder="m('lien-placeholder')"
          v-model="details.structSiteWeb"
        />
        <span>{{ m("lien") }}</span>
      </label>
      <br />
      <button :disabled="isButtonDisabled" class="btn-valider" @click="updateInfo">
        {{ m("valider") }}
      </button>
    </div>
  </div>
</template>
