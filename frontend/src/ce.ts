import DetailEtab from "./components/DetailEtab.ce.vue";
import Etab from "./components/Etab.ce.vue";
import ImageCropper from "./components/ImageCropper.ce.vue";
import ListEtab from "./components/ListEtab.ce.vue";
import I18nHost from "./components/I18nHost.ce.vue";
import { defineCustomElement } from "vue";

customElements.define(
    "detail-etab-ce",
    defineCustomElement(DetailEtab)
)
customElements.define(
    "etab-ce",
    defineCustomElement(Etab)
)
customElements.define(
    "list-etab-ce",
    defineCustomElement(ListEtab)
)
customElements.define(
    "image-cropper-ce",
    defineCustomElement(ImageCropper)
)
customElements.define(
    "i18n-ce",
    defineCustomElement(I18nHost)
)
