import { actionTypes } from '../actions/loginPageActions';

const initialState = {
    userId: window.location.href.substr(window.location.href.lastIndexOf('/') + 1),
    loginSuccess: false
};
export default (state = initialState, action) => {
    switch (action.type) {
        case actionTypes.SUBMIT_LOGIN:
            initialState.userId = action.userId;
            initialState.loginSuccess = action.loginSuccess;
            console.log("submitted: ", initialState);
            return {
                userId: initialState.userId,
                loginSuccess: initialState.loginSuccess,
            };
        default:
            return { state };
    }
};
