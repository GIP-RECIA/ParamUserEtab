<script setup>
import { ref, onMounted, computed, onBeforeUnmount, onUpdated } from "vue";
import DetailEtab from "./DetailEtab.vue";
import ListEtab from "./ListEtab.vue";
import axios from "axios";

// const etabs = generateTable();
const parametab = ref([]); // ajouté pour testé API
const etabJson = ref([]);
const currentEtab = ref("");
const windowWidth = ref(window.innerWidth);
const isVisible = ref(false);
const nameEtabSelected = ref("");

// console.log("cr etab : ", currentEtab);

onMounted(async () => {
  const res = await axios.get("/parametab/");
  parametab.value = res.data; // ajouté pour testé API
  //etabJson.value = res.data; // commenté pour testé API
  // Access the list of "isMemberOf"
  etabJson.value = parametab.value[0].isMemberOf; // ajouté pour testé API

  console.log("isMemberOfList: ", etabJson.value); // ajouté pour testé API
  //console.log("currentEtab: ", parametab.value[0].currentStruct); // ajouté pour testé API
  //currentEtab.value = etabJson.value[0]; // commenté pour testé API
  currentEtab.value = parametab.value[0].currentStruct; // ajouté pour testé API
  window.addEventListener("resize", handleResize);
});

onUpdated(() => {
  let id = document.getElementById(currentEtab.value); // ajouté pour testé API, avant c'était value.id
  if (id != null) {
    id.classList.add("active");
  } else {
    const findName = filteredName();
    nameEtabSelected.value = findName;
  }
  let listEtab = document.querySelector(".list");
  let activeElement = document.querySelector(".content .active");

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
  window.removeEventListener("resize", handleResize);
});

const isMobile = computed(() => {
  return windowWidth.value < 1024;
});

const handleResize = () => {
  windowWidth.value = window.innerWidth;
};

function filteredName() {
  let name = "";
  if (!etabJson) {
    return [];
  }
  name = etabJson.value.find((etab) => etab.idSiren.toString() === currentEtab.value);
  return name.etabName;
}

function select(e, isSelected) {
  let id = e.target.getAttribute("id");
  let selectedEtab = etabJson.value.find((etab) => etab.idSiren.toString() === id);

  if (selectedEtab !== currentEtab.value) {
    // return true
    // remove active class from the previously selected item

    const prevItem = document.getElementById(currentEtab.value);
    if (prevItem) {
      currentEtab.value = selectedEtab.idSiren;
      prevItem.classList.remove("active");
    }
    // set the new selected item and apply active class to it
    // currentEtab.value = selectedEtab;
    e.target.classList.add("active");
  }

  if (isSelected) {
    isVisible.value = false;
    nameEtabSelected.value = selectedEtab.etabName;
    e.target.classList.add("active");
  }
}
</script>

<template>
  <div class="list" v-if="!isMobile">
    <ListEtab
      class-input="input-search"
      class-li="item etab"
      class-div="list-etab"
      :dataJson="etabJson"
      :dataCurrent="currentEtab"
      @selectEtab="select($event, false)"
    ></ListEtab>
  </div>
  <div class="dropdown-wrapper" v-else>
    <div @click="isVisible = !isVisible" class="selected-etab">
      <span>{{ nameEtabSelected }}</span>
    </div>
    <div v-if="isVisible" class="dropdown-popover">
      <ListEtab
        class-input="search-etab"
        class-li="opt-list"
        class-div="options"
        :dataJson="etabJson"
        @selectEtab="select($event, true)"
      ></ListEtab>
    </div>
  </div>
  <div class="detail">
    <DetailEtab :detail="currentEtab"></DetailEtab>
  </div>
</template>
