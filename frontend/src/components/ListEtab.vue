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
  const filteredData = props.dataJson.filter((etab) =>
    etab.etabName.toLowerCase().includes(input.value.toLowerCase())
  );

  return filteredData.sort((a, b) =>
    a.etabName.localeCompare(b.etabName)
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
      v-model="input"
      :class="classInput"
      type="text"
      :placeholder="m('recherche')"
    />
  </div>
  <div :class="classDiv">
    <ul class="content">
      <li
        v-for="etab in filteredData"
        :id="etab.idSiren"
        :key="etab"
        :class="classLi"
        @click="selected"
      >
        {{ etab.etabName }}
      </li>
    </ul>
  </div>

  <div v-if="input && filteredData.length === 0" class="item error">
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
