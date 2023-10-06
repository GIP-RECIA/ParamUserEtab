import oidc from '@uportal/open-id-connect';
import axios from 'axios';

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

export { getParametab, getDetailEtab };
