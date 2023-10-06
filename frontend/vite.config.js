import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueI18n from "@intlify/unplugin-vue-i18n/vite";

// https://vitejs.dev/config/
export default defineConfig({
  base:"/indah/ui",
  plugins: [
    vue({
      template: {
        compilerOptions: {
          isCustomElement: (tag) => tag.includes("-ce"),
        },
      },
    }),
    vueI18n({}),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  // server: {
  //   proxy: {
  //     "/indah/test/api": {
  //       target: "http://10.209.28.15:8090",
  //       changeOrigin: true,
  //       ws: true
  //       //rewrite: (path) => path.replace(/^\/parametab/, '') // mettre en commentaire pour afficher /parametab/$id
  //     },
  //   },
  // },
});
