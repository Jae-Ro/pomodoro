import { actionTypes } from '../actions/userModalActions';
// import axios from 'axios';
// import { actionCreators } from '../actions/projectModalActions';

const initialState = {
    userModalOpen: false,
    userTableData: [],
    selectedUser: {},
    editUser: false,
};

export default (state = initialState, action) => {
    switch (action.type) {
        case actionTypes.OPEN_USER_MODAL:
            return {
                userModalOpen: true,
            };
        case actionTypes.CLOSE_USER_MODAL:
            return {
                userModalOpen: false,
            };
        case actionTypes.SUBMIT_USER_MODAL:
            initialState.editUser = false;
            return {
                userModalOpen: false,
                editUser: false,
            };

        case actionTypes.EDIT_USER_MODAL:
            initialState.selectedUser = { id: action.userId, firstName: action.userFn, lastName: action.userLn, email: action.userEmail };
            initialState.editUser = true;
            return {
                selectedUser: initialState.selectedUser,
                userModalOpen: true,
                editUser: initialState.editUser,
            };

        case actionTypes.DELETE_USER_MODAL:
            // let foundIndex = initialState.userTableData.findIndex(x => x.id === action.userId);
            // console.log(foundIndex);
            // if (foundIndex !== -1) initialState.userTableData.splice(foundIndex, 1);
            return {
                userModalOpen: false,
            };
        default:
            return state;
    }
};
