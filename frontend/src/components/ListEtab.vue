<script setup lang="ts">
import { ref, computed, type Ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const m = (key: string): string => t(`list-etab.${key}`);

const props = defineProps<{
  dataJson: any[];
  classInput: string;
  classLi: string;
  classDiv: string;
  dataCurrent: string;
}>();

const emit = defineEmits(["selectEtab"]);
let input: Ref<string> = ref("");

function filteredList(): any[] {
  if (!props.dataJson) {
    console.log("props datajson null");
    return [];
  }
  const filteredData = props.dataJson.filter((etab: any) =>
    etab.etabName.toLowerCase().includes(input.value.toLowerCase())
  );
  console.log("list-etab: ", filteredData);

  return filteredData.sort((a: any, b: any) => a.etabName.localeCompare(b.etabName));
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
