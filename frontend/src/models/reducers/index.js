import { combineReducers } from 'redux';

import userModal from '../../models/reducers/userModal';
import projectModal from '../../models/reducers/projectModal';
import loginPage from '../../models/reducers/loginPage';

export default combineReducers({
    userModal,
    projectModal,
    loginPage
});
