<script setup lang="ts">
import { ref, watchEffect, onMounted, onUnmounted, watch, defineEmits } from "vue";
import Cropper from "cropperjs";
import axios from "axios";
import { useI18n } from "vue-i18n";
import type { StructureDetail } from "@/types/structureType";

const { t } = useI18n();
const m = (key: string): string => t(`image-cropper.${key}`);

const props = defineProps<{
  imageUrl: string;
  idEtab: string;
  detailEtab: StructureDetail;
}>();

const imageInput = ref<any>(null);
const selectedFile = ref<any>(null);
const imageSrc = ref<string | ArrayBuffer | null>();
const img = ref<any>(null);
const fileReader = new FileReader();
const open = ref<boolean>(false);
let cropper: any = null;

fileReader.onload = (event: ProgressEvent<FileReader>) => {
  imageSrc.value = event?.target?.result;
};

const fileChanged = (e) => {
  const files = e.target.files || e.dataTransfer.files;
  if (files.length) {
    selectedFile.value = files[0];
  }
};

const emit = defineEmits(["updated"]);

onMounted(() => {
  if (img.value) {
    cropper = new Cropper(img.value, {
      aspectRatio: 270 / 120,
      viewMode: 2,
      background: false,
    });
  }
});

onUnmounted(() => {
  if (cropper) {
    cropper.destroy();
    cropper = null;
  }
});

watchEffect(() => {
  if (selectedFile.value) {
    fileReader.readAsDataURL(selectedFile.value);
  } else {
    imageSrc.value = null;
  }

  // set image par defaut if structLogo is null
  // if (props.detailEtab.structLogo == null) {
  //   props.detailEtab.structLogo = "/src/assets/flower.jpg";
  // }
});

watch(
  imageSrc,
  () => {
    if (imageSrc.value && img.value) {
      if (cropper) {
        cropper.replace(imageSrc.value);
      } else {
        cropper = new Cropper(img.value, {
          aspectRatio: 270 / 120,
          viewMode: 2,
          background: false,
          zoomable: true,
          preview: "#previewImg",
        });
      }
    }
  },
  {
    flush: "post",
  }
);

const closeModal = () => {
  open.value = false;
  selectedFile.value = null;
  if (cropper) {
    cropper.destroy();
    cropper = null;
  }
};

const cropImage = () => {
  cropper.getCroppedCanvas().toBlob((blob) => {
    const formData = new FormData();

    // append DTO as JSON string
    formData.append("details", JSON.stringify(props.detailEtab));

    // add name for the image
    formData.append("name", "image-name-" + new Date().getTime());

    // append image file
    formData.append("file", blob, "logo." + blob.type.split("/")[1]);
    const url = `/parametab/fileUpload/${props.idEtab}`;

    axios
      .post(url, formData)
      .then((response) => {})
      .catch(function (error) {});
  }, "image/jpeg");
};
</script>

<template>
  <div class="avatar-upload">
    <div class="avatar-edit">
      <input
        ref="imageInput"
        type="file"
        accept=".jpg, .jpeg, .png"
        :style="{ display: 'none' }"
        @change="fileChanged"
      />
      <label @click="open = true"></label>
    </div>
    <div class="avatar-preview">
      <img class="imagePreview" :src="imageUrl" alt="" width="270" height="120" />
    </div>
  </div>

  <!-- Modal -->
  <Teleport to="body">
    <div v-if="open" class="modal">
      <input id="idEtab" type="hidden" name="idEtab" :value="idEtab" />
      <div>
        <div class="close">
          <button type="button" class="close" @click="closeModal">x</button>
        </div>
        <div class="images">
          <div v-show="imageSrc" class="cropImg">
            <img ref="img" :src="imageSrc" alt="" width="280" />
          </div>
          <div class="previewImg">
            <div id="previewImg">
              <img :src="imageUrl" alt="" width="270" height="120" />
            </div>
          </div>
        </div>
        <div class="buttons">
          <button class="btn-selectImg" @click="imageInput.click()">
            {{ m("selectionner-image") }}
          </button>
          <button v-show="imageSrc" class="btn-cropImg" @click="cropImage">
            {{ m("appliquer") }}
          </button>
          <button class="btn-close" @click="closeModal">{{ m("fermer") }}</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style>
.btn-selectImg,
.btn-cropImg,
.btn-close {
  padding: 5px;
  cursor: pointer;
}

.buttons > button {
  margin-right: 5px;
}

.cropImg {
  flex: 1;
  padding-right: 10px;
}

.previewImg {
  flex: 1;
  margin: auto;
}

#previewImg {
  width: 270px;
  height: 120px;
  overflow: hidden;
  margin: auto;
  border: 0 solid #eee;
  border-radius: 4px;
}

/* .modal {
  position: fixed;
  float: left;
  left: 50%;
  top: 25%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.5);
  box-shadow: 0 4px 16px #00000026;
  padding: 30px;
  border-radius: 8px;
  width: 450px;
  z-index: 999;
} */

.modal {
  position: fixed;
  float: left;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal > div {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: white;
  width: 550px;
  padding: 20px;
  border-radius: 8px;
  bottom: 10%;
  max-width: 90%;
}

.close {
  margin-left: auto;
  cursor: pointer;
}

button.close {
  border: 0;
  opacity: 0.4;
  /* background: transparent; */
  background: darkgrey;
  font-weight: bold;
  line-height: 1;
  padding-right: 8px;
  padding-left: 8px;
  border-radius: 100%;
  height: 20px;
}

.buttons {
  margin: 15px;
}

.images {
  display: flex;
  flex-wrap: wrap;
  max-width: 100%;
  overflow: hidden;
}
</style>
