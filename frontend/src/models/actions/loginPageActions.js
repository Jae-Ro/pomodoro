
const SUBMIT_LOGIN = 'frontend/modal/SUBMIT_LOGIN';


export const actionTypes = {
    SUBMIT_LOGIN
};

const submitLogin = (id, success) => ({
    userId: id,
    logingSuccess: success,
    type: SUBMIT_LOGIN
});

export const loginActionCreators = {
    submitLogin
};