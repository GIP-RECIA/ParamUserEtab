<script setup>

import { ref } from 'vue'

const count = ref(0)
const btnClass = ref('btn')

function increment() {
  count.value++
}

function initialize() {
  count.value = 0
}

const treeData = ref (
  {
  name: "First tree",
  children: [
    { name : 'first content'},
    { name : 'blabla content'}
  ]
}
)

// const treeData = ref ([
//   {
//   nameParent: "First tree",
//   children: [
//     { name : 'first content'}
//   ]
// }, {
//   nameParent: "Second tree",
//   children: [
//     { name : 'second content'}
//   ]
// }, {
//   nameParent: "Third tree",
//   children: [
//     { name : 'third content'}
//   ]
// }
// ])

</script>

<template>
  
      <!-- <input @input="recherche" type="text" placeholder="Recherche"> -->
    <!-- <input type="text" v-model="input" placeholder="Search etab..."/> -->
      <!-- <ul>
        <li v-for="item in etabs">{{ item.name }}</li> -->
        <!-- <li v-for="etab in filteredList()">{{ etab.name }}</li> -->
      <!-- </ul> -->

  <button :class="btnClass" @click="increment">count is : {{ count }}</button>
  <button @click="initialize">reinitialize</button>
</template>
<!--
todos.value.push({id: id++, text: newTodo.value})
todos.value = todos.value.filter((t) => t !== todo)
-->


<style>
.btn {
  color: blue
}
</style>
