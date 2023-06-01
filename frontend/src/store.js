import { reactive, toRefs } from "vue";
import axios from "axios";

const url = "http://localhost:3000/etablissements";

const state = reactive({
    etabJson: [],
    selectedEtab: null
})

export default function useEtabJSON() {

    const fetchEtabs = async () => {
        state.etabJson = await axios.get(url);
    }

    return {
        ...toRefs(state),
        fetchEtabs
    }
}