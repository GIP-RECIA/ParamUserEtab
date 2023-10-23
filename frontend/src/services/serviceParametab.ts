import type { StructureDetail } from '@/types/structureType';
import oidc from '@uportal/open-id-connect';
import axios from 'axios';
import Swal from 'sweetalert2';

const getToken = async (userInfoApiUrl: string): Promise<string | undefined> => {
  try {
    const { encoded, decoded } = await oidc({
      userInfoApiUrl: userInfoApiUrl,
    });
    console.log(decoded);
    return encoded;
  } catch (error) {
    console.error('error: ', error);
  }
};

const getParametab = async (url: string, userInfoApiUrl: string) => {
  console.log('userinfo: ', userInfoApiUrl);
  console.log('url : ', url);
  return await axios.get(`${url}`, {
    headers: {
      Authorization: `Bearer ${await getToken(userInfoApiUrl)}`,
      'content-type': 'application/jwt',
    },
  });
};

const getDetailEtab = async (url: string) => {
  return await axios.get(`${url}`);
};

const updateEtab = async (url: string, details: StructureDetail, userInfoApiUrl: string) => {
  console.log('url : ', url);
  console.log('details : ', JSON.stringify(details));
  return await axios.put(`${url}`, `${JSON.stringify(details)}`, {
    headers: {
      Authorization: `Bearer ${await getToken(userInfoApiUrl)}`,
      'content-type': 'application/json',
    },
  });
};

export { getParametab, getDetailEtab, updateEtab };
