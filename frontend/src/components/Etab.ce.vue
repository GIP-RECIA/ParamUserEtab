<script setup lang="ts">
import axios from 'axios';
import { type HTMLAttributes, computed, onBeforeUnmount, onBeforeUpdate, onMounted, onUpdated, ref } from 'vue';

const parametab = ref<any>([]);
const etabJson = ref<string>('');
const currentEtab = ref<string>('');
const windowWidth = ref<number>(window.innerWidth);
const isVisible = ref<boolean>(false);
const nameEtabSelected = ref<string>('');
const findEtab = ref<any[]>([]);

onMounted(async () => {
  const res = await axios.get('/parametab/');
  parametab.value = res.data; // ajouté pour testé API
  // Access the list of "isMemberOf"

  // ajouté pour testé API
  etabJson.value = JSON.stringify(parametab.value.isMemberOf);
  currentEtab.value = parametab.value.currentStruct;
  findEtab.value = JSON.parse(etabJson.value);
  window.addEventListener('resize', handleResize);
});

onUpdated(() => {
  let id = document.getElementById(currentEtab.value);
  if (id != null) {
    id.classList.add('active');
  } else {
    const findName = filteredName();
    nameEtabSelected.value = findName;
  }
  let listEtab: HTMLElement | null = document.querySelector('.list');
  let activeElement: HTMLElement | null = document.querySelector('.content .active');

  if (listEtab) {
    if (activeElement) {
      let scrollTop = activeElement.offsetTop - listEtab.offsetTop;
      let scrollBottom = scrollTop + activeElement.offsetHeight;
      let maxScrollTop = listEtab.scrollHeight - listEtab.offsetHeight;
      if (scrollTop < 0) {
        scrollTop = 0;
      } else if (scrollBottom > listEtab.offsetHeight) {
        scrollTop = maxScrollTop;
      }
      listEtab.scrollTop = scrollTop;
    }
  }
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
});

const isMobile = computed(() => {
  return windowWidth.value < 1024;
});

const handleResize = () => {
  windowWidth.value = window.innerWidth;
};

function filteredName() {
  let name: any = '';
  if (!etabJson.value) {
    return [];
  }
  name = findEtab.value.find((etab: any) => etab.idSiren.toString() === currentEtab.value);
  return name.etabName;
}

function select(payload: CustomEvent) {
  let getID = payload.detail[0].idSiren;
  let getName = payload.detail[0].etabName;
  let isSelected = payload.detail[1];

  if (getID !== currentEtab.value) {
    currentEtab.value = getID;
  }

  if (isSelected) {
    isVisible.value = false;
    nameEtabSelected.value = getName;
    currentEtab.value = getID;
  }
}
</script>

<template>
  <div v-bind="$attrs">
    <div v-if="!isMobile" class="list">
      <list-etab-ce
        class-input="input-search"
        class-li="item etab"
        class-div="list-etab"
        v-bind:data-json="etabJson"
        :data-current="currentEtab"
        @selectEtab="select"
      ></list-etab-ce>
    </div>
    <div v-else class="dropdown-wrapper">
      <div class="selected-etab" @click="isVisible = !isVisible">
        <span>{{ nameEtabSelected }}</span>
      </div>
      <div v-if="isVisible" class="dropdown-popover">
        <list-etab-ce
          class-input="search-etab"
          class-li="opt-list"
          class-div="options"
          v-bind:data-json="etabJson"
          :data-current="currentEtab"
          @selectEtab="select"
        ></list-etab-ce>
      </div>
    </div>
    <div class="detail">
      <detail-etab-ce :detail="currentEtab"></detail-etab-ce>
    </div>
  </div>
</template>
<style>
@import '../assets/detailList.css';
@import '../assets/list.css';
@import '../assets/main.css';
</style>
