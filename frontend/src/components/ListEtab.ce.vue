<script setup lang="ts">
import { type Ref, computed, ref, watchEffect } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const m = (key: string): string => t(`list-etab.${key}`);

const props = defineProps<{
  dataJson: string;
  classInput: string;
  classLi: string;
  classDiv: string;
  dataCurrent: string;
}>();

const emit = defineEmits<(e: 'selectEtab', payload: any, isSelected: boolean) => void>();
let input: Ref<string> = ref('');

function filteredList(): any[] {
  if (!props.dataJson) {
    console.log('props datajson null');
    return [];
  }
  const jsonData = JSON.parse(props.dataJson);
  const filteredData = jsonData.filter((etab: any) => etab.etabName.toLowerCase().includes(input.value.toLowerCase()));

  return filteredData.sort((a: any, b: any) => a.etabName.localeCompare(b.etabName));
}

function selected(id: string) {
  emit('selectEtab', id, false);
}

const filteredData = computed(() => filteredList());
</script>

<template>
  <div class="search-bar">
    <input v-model="input" :class="classInput" type="text" :placeholder="m('recherche')" />
  </div>
  <div :class="classDiv">
    <ul class="content">
      <li
        v-for="etab in filteredData"
        :id="etab.idSiren"
        :key="etab"
        :class="[classLi, etab.idSiren == dataCurrent ? 'active' : '']"
        @click="selected(etab)"
      >
        {{ etab.etabName }}
      </li>
    </ul>
  </div>

  <div v-if="input && filteredData.length === 0" class="item error">
    <p>{{ m('aucun-res') }}</p>
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
<style scoped>
@import '../assets/base.css';

.container {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.list {
  flex: 1 30%;
  background-color: white;
  border-radius: 5px;
  margin: 10px;
  overflow-y: scroll;
  /* box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.12), 0 1px 6px 0 rgba(0, 0, 0, 0.12); */
}

.text {
  text-align: center;
}

.item {
  cursor: pointer;
  line-height: 1.5;
}

.bold {
  font-weight: bold;
}

.input-search {
  display: block;
  width: 350px;
  margin: 20px auto;
  padding: 10px 45px;
  background: white url('search-icon.svg') no-repeat 15px center;
  background-size: 15px 15px;
  font-size: 16px;
  border: none;
  border-radius: 5px;
  box-shadow:
    rgba(50, 50, 93, 0.25) 0px 2px 5px -1px,
    rgba(0, 0, 0, 0.3) 0px 1px 3px -1px;
}

.item {
  margin: 0 auto 5px auto;
  padding: 10px 20px;
  color: black;
  border-radius: 5px;
  box-shadow:
    rgba(0, 0, 0, 0.1) 0px 1px 3px 0px,
    rgba(0, 0, 0, 0.06) 0px 1px 2px 0px;
}

.etab {
  background-color: white;
  cursor: pointer;
  list-style-type: none;
}

ul {
  padding-left: 0px;
}

ul .etab:hover {
  background: #eeeeee;
}

.error {
  background-color: tomato;
}

.selected {
  background-color: green;
}

.active {
  background-color: #d4d4d4;
  color: black;
}

.search-bar {
  background-color: white;
  position: sticky;
  top: 0;
  z-index: 1;
}

@media (max-width: 1023px) {
  .person-list {
    display: none;
  }
  .list-select {
    flex: 1 30%;
    background-color: white;
    border-radius: 5px;
    margin: 10px;
    overflow-y: scroll;
  }
}

.dropdown-wrapper {
  flex: 1 30%;
  border-radius: 5px;
  margin: 10px;
  overflow-y: scroll;
}
.dropdown-wrapper .selected-etab {
  background: white url('chevron-down.svg') no-repeat right;
  background-position-x: right 15px;
  background-size: 15px 15px;
  border: 2px solid lightgray;
  border-radius: 5px;
  padding: 5px 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}
.dropdown-wrapper .dropdown-popover {
  position: relative;
  border: 2px solid lightgray;
  left: 0;
  right: 0;
  background-color: #fff;
  max-width: 100%;
  padding: 10px;
  border-top: none;
  border-bottom-right-radius: 5px;
  border-bottom-left-radius: 5px;
}
.dropdown-wrapper .search-etab {
  width: 100%;
  height: 30px;
  border: 2px solid lightgray;
  font-size: 16px;
  padding: 10px 45px;
  background: white url('search-icon.svg') no-repeat 15px center;
  background-size: 15px 15px;
  border-radius: 5px;
}
.dropdown-wrapper .options {
  width: 100%;
}
.dropdown-wrapper .options ul {
  list-style: none;
  text-align: left;
  padding-left: 0px;
  max-height: 180px;
  overflow-y: scroll;
  overflow-x: hidden;
  top: 10px;
}
.dropdown-wrapper .options ul .opt-list {
  width: 100%;
  border-bottom: 1px solid lightgray;
  padding: 10px;
  cursor: pointer;
}
.dropdown-wrapper .options ul .opt-list:hover {
  background: #d4d4d4;
  /* color: #fff; */
  font-weight: bold;
}
</style>
