<script setup>
import { ref, computed } from 'vue'

const props = defineProps({ 
    model: Object
})

const isOpen = ref(false)
const isFolder = computed(() => {
    return props.model.children && props.model.children.length
})

function toogle() {
    isOpen.value = !isOpen.value
}

function changeType() {
    if ( !isFolder.value ) {
        props.model.children = []
        isOpen.value = true
    }
}

</script>

<template>
    <li>
        <div :class="{bold: isFolder}" @click="toogle" @dblclick="changeType">{{ model.name }}
            <span v-if="isFolder">[{{ isOpen ? '-' : '+' }}]</span>
        </div>

        <ul v-show="isOpen" v-if="isFolder">
            <TreeItem class="item" v-for="model in model.children" :model="model">
            </TreeItem>
        
        </ul>
    </li>

    <!-- <li v-for="models in model" :model="model">
        <div :class="{bold: isFolder}" @click="toogle" @dblclick="changeType">{{ models.nameParent }}
            <span v-if="isFolder">[{{ isOpen ? '-' : '+' }}]</span>
        </div>

        <ul v-show="isOpen" v-if="isFolder">
            <TreeItem class="item" v-for="modelChild in models" :model="modelChild">
            </TreeItem>
        
        </ul>
        <ul v-else>Error
        </ul>
    </li> -->

</template>