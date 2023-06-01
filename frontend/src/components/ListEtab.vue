<script setup>
import { ref, computed } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const m = (key) => t(`list-etab.${key}`);

const props = defineProps({
  dataJson: Array,
  classInput: String,
  classLi: String,
  classDiv: String,
  dataCurrent: String,
});

const emit = defineEmits(["selectEtab"]);
let input = ref("");

function filteredList() {
  if (!props.dataJson) {
    return [];
  }
  return props.dataJson.filter((etab) =>
    etab.etabName.toLowerCase().includes(input.value.toLowerCase())
  );
}

function selected(e) {
  emit("selectEtab", e, false);
}

const filteredData = computed(() => filteredList());
</script>

<template>
  <div class="search-bar">
    <input
      :class="classInput"
      type="text"
      :placeholder="m('recherche')"
      v-model="input"
    />
  </div>
  <div :class="classDiv">
    <ul class="content">
      <li
        :class="classLi"
        v-for="etab in filteredData"
        :key="etab"
        :id="etab.idSiren"
        @click="selected"
      >
        {{ etab.etabName }}
      </li>
    </ul>
  </div>

  <div class="item error" v-if="input && filteredData.length === 0">
    <p>{{ m("aucun-res") }}</p>
  </div>
</template>

<!--
  <input
        class="search-etab"
        type="text"
        v-model="input"
        placeholder="Recherche etab..."
      />
      <div class="options">
        <ul>
          <li
            class="opt-list"
            v-for="etab in filteredList()"
            :key="etab"
            :id="etab.id"
            @click="select($event, true)"
          >
            {{ etab.name }}
          </li>
        </ul>
      </div> 

  <input
  class="input-search"
  type="text"
  placeholder="Recherche etab..."
  v-model="input"
/>
</div>
<div class="list-etab">
<li
  class="item etab"
  v-for="etab in filteredData"
  :key="etab"
  :id="etab.id"
  @click="selected"
>
  {{ etab.name }}
</li>


-->
